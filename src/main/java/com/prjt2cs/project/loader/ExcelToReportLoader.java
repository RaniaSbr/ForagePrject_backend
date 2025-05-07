package com.prjt2cs.project.loader;

import com.prjt2cs.project.model.DailyCost;
import com.prjt2cs.project.model.Operation;
import com.prjt2cs.project.model.Report;
import com.prjt2cs.project.service.ExcelReader;
import com.prjt2cs.project.repository.ReportRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ExcelToReportLoader implements CommandLineRunner {

    private final ReportRepository reportRepository;
    private final DailyCostLoader dailyCostLoader;

    public ExcelToReportLoader(ReportRepository reportRepository, DailyCostLoader dailyCostLoader) {
        this.reportRepository = reportRepository;
        this.dailyCostLoader = dailyCostLoader;
    }

    @Override
    @Transactional // Ajout de l'annotation @Transactional pour garantir l'atomicité des opérations
    public void run(String... args) {
        try {
            ExcelReader excelReader = new ExcelReader();

            String fileName = "18.xlsx";
            int sheetIndex = 0; // Utilise explicitement la feuille 0

            // Lister les feuilles disponibles pour information
            excelReader.listSheets(fileName);
            System.out.println("Utilisation de la feuille à l'index " + sheetIndex);

            String startColumn = "CG";
            String endColumn = "CK";
            int rowIndex = 10;

            String act = excelReader.readCellRangeConcatenated(fileName, startColumn, endColumn, rowIndex, sheetIndex);
            String dep = excelReader.readCellRangeConcatenated(fileName, "U", "Z", 6, sheetIndex);
            String plannedOpe = excelReader.readCellRangeConcatenated(fileName, "O", "BC", 58, sheetIndex);
            Double drillingHours = Double
                    .parseDouble(excelReader.readCellRangeConcatenated(fileName, "BF", "BH", 6, sheetIndex));
            Double drillingProgress = Double
                    .parseDouble(excelReader.readCellRangeConcatenated(fileName, "AW", "BA", 6, sheetIndex));
            Double tvd = Double.parseDouble(excelReader.readCellRangeConcatenated(fileName, "AF", "AK", 6, sheetIndex));
            Double day = Double
                    .parseDouble(excelReader.readCellRangeConcatenated(fileName, "BQ", "BY", 62, sheetIndex));
            if (act != null) {
                Report report = new Report();

                // Utiliser ArrayList au lieu de Arrays.asList() pour permettre l'ajout
                // d'éléments
                List<String> remarksList = new ArrayList<>();

                for (int row = 46; row <= 56; row++) {
                    try {
                        // Read all data first with explicit sheet index
                        String rowRemark = excelReader.readCellRangeConcatenated(fileName, "B", "BG", row, sheetIndex);

                        // Print debug info for this row
                        System.out.println("\nProcessing row " + row + ":");
                        System.out.println("  rowRemark: " + rowRemark);

                        // Skip entirely empty rows (no start time)
                        if (rowRemark == null || rowRemark.trim().isEmpty()) {
                            System.out.println("Skipping row " + row + " - missing remark");
                            continue; // Passer à la ligne suivante si rowRemark est vide
                        }

                        remarksList.add(rowRemark.trim());
                        System.out.println("Successfully added remark from row " + row);

                    } catch (Exception e) {
                        System.out.println("Unexpected error processing row " + row + ": " + e.getMessage());
                        e.printStackTrace();
                        // Continue with next row even if there's an error with this one
                    }
                }

                // Définir les remarques après avoir ajouté toutes les entrées valides
                report.setRemarks(remarksList);

                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                try {
                    // Use default values if data is missing
                    double depth = 0.0;
                    if (dep != null && !dep.trim().isEmpty()) {
                        try {
                            depth = Double.parseDouble(dep.trim());
                        } catch (NumberFormatException e) {
                            System.out.println("Error parsing depth: " + dep + ", using default 0.0");
                        }
                    }
                    report.setDepth(depth);
                    report.setDrillingProgress(drillingProgress);
                    report.setDrillingHours(drillingHours);
                    report.setTvd(tvd);
                    report.setDay(day);

                    report.setPlannedOperation(plannedOpe != null ? plannedOpe.trim() : "Planned Operation");

                    // Gestion améliorée du parsing de la date
                    String dateString = excelReader.readCellRangeConcatenated(fileName, "DG", "DM", 4, 0);
                    LocalDate parsedDate = null;

                    if (dateString != null && !dateString.trim().isEmpty()) {
                        dateString = dateString.trim();
                        // Essayer différents formats de date courants
                        List<String> dateFormats = Arrays.asList(
                                "yyyy-MM-dd", "dd/MM/yyyy", "MM/dd/yyyy", "dd-MM-yyyy", "yyyy/MM/dd");

                        for (String format : dateFormats) {
                            try {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                                parsedDate = LocalDate.parse(dateString, formatter);
                                System.out.println("Date successfully parsed with format: " + format);
                                break; // Sortir de la boucle si le parsing réussit
                            } catch (DateTimeParseException e) {
                                // Essayer le prochain format
                            }
                        }

                        if (parsedDate == null) {
                            System.out.println("Could not parse date string: " + dateString + " - using current date");
                            parsedDate = LocalDate.now();
                        }
                    } else {
                        System.out.println("Date string is empty - using current date");
                        parsedDate = LocalDate.now();
                    }

                    report.setDate(parsedDate);

                    // NE PAS sauvegarder le rapport ici - attendez que toutes les opérations et
                    // coûts soient configurés
                    System.out.println("Report created: " + act + ", depth=" + depth + ", planned=" + plannedOpe
                            + ", date=" + parsedDate);

                    // Counter for successful operations
                    int operationsAdded = 0;

                    // Custom formatter to handle both standard time and "24:00" format
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

                    // Parcourir les lignes de 22 à 43
                    for (int row = 22; row <= 43; row++) {
                        try {
                            // Read all data first with explicit sheet index
                            String code = excelReader.readCellRangeConcatenated(fileName, "AW", "AZ", row, sheetIndex);
                            String startTimeStr = excelReader.readCellRangeConcatenated(fileName, "B", "D", row,
                                    sheetIndex);
                            String endTimeStr = excelReader.readCellRangeConcatenated(fileName, "F", "H", row,
                                    sheetIndex);
                            String description = excelReader.readCellRangeConcatenated(fileName, "I", "AV", row,
                                    sheetIndex);
                            String initialDepthStr = excelReader.readCellRangeConcatenated(fileName, "BA", "BE", row,
                                    sheetIndex);
                            String finalDepthStr = excelReader.readCellRangeConcatenated(fileName, "BF", "BK", row,
                                    sheetIndex);
                            String rateStr = excelReader.readCellRangeConcatenated(fileName, "BR", "BT", row,
                                    sheetIndex);

                            // Print debug info for this row
                            System.out.println("\nProcessing row " + row + ":");
                            System.out.println("  Code: " + code);
                            System.out.println("  Start time: " + startTimeStr);
                            System.out.println("  End time: " + endTimeStr);
                            System.out.println("  Initial depth: " + initialDepthStr);
                            System.out.println("  Final depth: " + finalDepthStr);
                            System.out.println("  Rate: " + rateStr);

                            // Skip entirely empty rows (no start time)
                            if (startTimeStr == null || startTimeStr.trim().isEmpty()) {
                                System.out.println("Skipping row " + row + " - missing start time");
                                continue; // Passer à la ligne suivante si startTime est vide
                            }

                            // Create operation with proper parsing and default values
                            Operation op = new Operation();
                            op.setCode(code != null && !code.trim().isEmpty() ? code.trim() : "CODE-" + row);

                            // Parse start time
                            LocalTime startTime;
                            try {
                                startTimeStr = startTimeStr.trim();
                                startTime = LocalTime.parse(startTimeStr);
                            } catch (DateTimeParseException e) {
                                try {
                                    // Try alternative format
                                    startTime = LocalTime.parse(startTimeStr, timeFormatter);
                                } catch (DateTimeParseException e2) {
                                    System.out.println("Cannot parse start time: " + startTimeStr + " - skipping row");
                                    continue; // Passer à la ligne suivante si startTime ne peut pas être analysé
                                }
                            }
                            op.setStartTime(startTime);

                            // Parse end time or use start time + 1 hour if missing
                            LocalTime endTime;
                            if (endTimeStr == null || endTimeStr.trim().isEmpty()) {
                                endTime = startTime.plusHours(1);
                                System.out.println("  No end time provided, using: " + endTime);
                            } else {
                                try {
                                    endTimeStr = endTimeStr.trim();
                                    // Special handling for "24:00"
                                    if (endTimeStr.equals("24:00")) {
                                        endTime = LocalTime.of(0, 0); // Midnight
                                    } else {
                                        endTime = LocalTime.parse(endTimeStr);
                                    }
                                } catch (DateTimeParseException e) {
                                    try {
                                        // Try alternative format
                                        endTime = LocalTime.parse(endTimeStr, timeFormatter);
                                    } catch (DateTimeParseException e2) {
                                        // Use start time + 1 hour as fallback
                                        endTime = startTime.plusHours(1);
                                        System.out.println(
                                                "  Cannot parse end time: " + endTimeStr + ", using: " + endTime);
                                    }
                                }
                            }
                            op.setEndTime(endTime);

                            // Set description or default
                            op.setDescription(description != null && !description.trim().isEmpty() ? description.trim()
                                    : "Operation at row " + row);

                            // Parse numeric values with defaults
                            double initialDepth = 0.0;
                            if (initialDepthStr != null && !initialDepthStr.trim().isEmpty()) {
                                try {
                                    initialDepth = Double.parseDouble(initialDepthStr.trim());
                                } catch (NumberFormatException e) {
                                    System.out.println("  Error parsing initial depth: " + initialDepthStr
                                            + ", using default 0.0");
                                }
                            }
                            op.setInitialDepth(initialDepth);

                            double finalDepth = initialDepth; // Default to initial depth
                            if (finalDepthStr != null && !finalDepthStr.trim().isEmpty()) {
                                try {
                                    finalDepth = Double.parseDouble(finalDepthStr.trim());
                                } catch (NumberFormatException e) {
                                    System.out.println("  Error parsing final depth: " + finalDepthStr + ", using "
                                            + initialDepth);
                                }
                            }
                            op.setFinalDepth(finalDepth);

                            // Vérifier si rateStr est null avant de l'assigner
                            op.setRate(rateStr != null ? rateStr : "");

                            // Utiliser la date du rapport plutôt que la date actuelle
                            op.setDate(parsedDate);

                            // Ajouter l'opération au rapport
                            report.addOperation(op);
                            operationsAdded++;
                            System.out.println("Successfully added operation from row " + row);

                        } catch (Exception e) {
                            System.out.println("Unexpected error processing row " + row + ": " + e.getMessage());
                            e.printStackTrace();
                            // Continue with next row even if there's an error with this one
                        }
                    }

                    // IMPORTANT: Sauvegarder d'abord le rapport pour lui donner un ID
                    Report savedReport = reportRepository.save(report);
                    System.out.println(
                            "\nReport saved with " + operationsAdded + " operations. ID: " + savedReport.getId());

                    // Maintenant que le rapport a un ID, importer les coûts quotidiens
                    int dailyCostSheetIndex = 1; // Feuille pour les coûts quotidiens
                    System.out.println("Utilisation de la feuille à l'index " + dailyCostSheetIndex
                            + " pour les coûts quotidiens");

                    try {
                        System.out.println("Importing daily costs from sheet " + dailyCostSheetIndex);
                        DailyCost dailyCost = dailyCostLoader.importDailyCostFromExcel(fileName, dailyCostSheetIndex,
                                savedReport.getId());

                        if (dailyCost != null) {
                            System.out.println(
                                    "Daily cost imported successfully. Total cost: " + dailyCost.getDailyCost());

                            // IMPORTANT: S'assurer que la relation bidirectionnelle est établie et
                            // persistée
                            savedReport.setDailyCost(dailyCost);
                            reportRepository.save(savedReport);
                            System.out.println("Report updated with daily cost reference. ID: " + savedReport.getId());
                        } else {
                            System.out
                                    .println("Warning: Daily cost is null - report saved without daily cost reference");
                        }
                    } catch (Exception e) {
                        System.out.println("Error importing daily costs: " + e.getMessage());
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    System.out.println("Error processing report: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Error reading Excel file - missing activity data");
            }
        } catch (Exception e) {
            System.out.println("Fatal error in Excel loading: " + e.getMessage());
            e.printStackTrace();
        }
    }
}