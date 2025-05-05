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

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ExcelToReportLoader implements CommandLineRunner {

    private final ReportRepository reportRepository;
    private final DailyCostLoader dailyCostLoader; // Ajouter cette déclaration

    public ExcelToReportLoader(ReportRepository reportRepository, DailyCostLoader dailyCostLoader) {
        this.reportRepository = reportRepository;
        this.dailyCostLoader = dailyCostLoader;

    }

    @Override
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
            String dep = excelReader.readCellRangeConcatenated(fileName, "BX", "CB", 10, sheetIndex);
            String cost = excelReader.readCellRangeConcatenated(fileName, "CJ", "CO", 58, sheetIndex);

            if (act != null) {
                Report report = new Report();
                try {
                    report.setActivity(act);

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

                    double costValue = 0.0;
                    if (cost != null && !cost.trim().isEmpty()) {
                        try {
                            costValue = Double.parseDouble(cost.trim());
                        } catch (NumberFormatException e) {
                            System.out.println("Error parsing cost: " + cost + ", using default 0.0");
                        }
                    }
                    report.setCost(costValue);
                    int dailyCostSheetIndex = 1; // Feuille pour les coûts quotidiens - à ajuster
                    System.out.println("Utilisation de la feuille à l'index " + dailyCostSheetIndex);

                    report.setDate(LocalDate.now());

                    // First save to get an ID for the report
                    reportRepository.save(report);
                    System.out.println("Initial report created: " + act + ", depth=" + depth + ", cost=" + costValue);

                    System.out.println("Utilisation de la feuille à l'index " + dailyCostSheetIndex
                            + " pour les coûts quotidiens");

                    try {
                        System.out.println("Importing daily costs from sheet " + dailyCostSheetIndex);
                        DailyCost dailyCost = dailyCostLoader.importDailyCostFromExcel(fileName, dailyCostSheetIndex,
                                report.getId());
                        System.out.println("Daily cost imported successfully. Total cost: " + dailyCost.getDailyCost());
                    } catch (Exception e) {
                        System.out.println("Error importing daily costs: " + e.getMessage());
                        e.printStackTrace();
                    }

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
                            } else {
                                System.out.println("  Missing initial depth, using default 0.0");
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
                            } else {
                                System.out.println("  Missing final depth, using initial depth: " + initialDepth);
                            }
                            op.setFinalDepth(finalDepth);

                            op.setRate(rateStr);

                            op.setDate(LocalDate.now());

                            // Add operation to report
                            op.setReport(report);

                            report.addOperation(op);
                            operationsAdded++;
                            System.out.println("Successfully added operation from row " + row);

                        } catch (Exception e) {
                            System.out.println("Unexpected error processing row " + row + ": " + e.getMessage());
                            e.printStackTrace();
                            // Continue with next row even if there's an error with this one
                        }
                    }

                    // Final save to persist operations
                    reportRepository.save(report);
                    System.out.println("\nReport updated with " + operationsAdded + " operations");

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