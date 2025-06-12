package com.prjt2cs.project.controller;

import com.prjt2cs.project.model.DailyCost;
import com.prjt2cs.project.model.Operation;
import com.prjt2cs.project.model.Report;
import com.prjt2cs.project.repository.ReportRepository;
import com.prjt2cs.project.repository.DailyCostRepository;
import com.prjt2cs.project.repository.OperationRepository;
import com.prjt2cs.project.model.Puit;
import com.prjt2cs.project.repository.PuitRepository;
import com.prjt2cs.project.service.ExcelReader;
import java.util.Base64;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportUploadController {

    private static final Logger logger = LoggerFactory.getLogger(ReportUploadController.class);

    private final ReportRepository reportRepository;
    private final OperationRepository operationRepository;
    private final DailyCostRepository dailyCostRepository;
    private final PuitRepository puitRepository;
    private final ExcelReader excelReader;

    public ReportUploadController(
            ReportRepository reportRepository,
            DailyCostRepository dailyCostRepository,
            OperationRepository operationRepository,
            ExcelReader excelReader,
            PuitRepository puitRepository) { // NOUVEAU
        this.dailyCostRepository = dailyCostRepository;
        this.reportRepository = reportRepository;
        this.operationRepository = operationRepository;
        this.excelReader = excelReader;
        this.puitRepository = puitRepository; // NOUVEAU
    }

    @PostMapping("/upload")
    public ResponseEntity<String> createReportFromExcel(@RequestParam("file") MultipartFile file,
            @RequestParam("puitId") String puitId) {

        try {
            // Copy file content to memory
            Puit puit = puitRepository.findById(puitId)
                    .orElseThrow(() -> new RuntimeException("Puit non trouvé avec l'ID: " + puitId));

            // Enregistrer le fichier Excel dans le modèle Report
            byte[] fileBytes = file.getBytes();

            // Create a new report
            Report report = new Report();
            report.setExcelFile(fileBytes);
            report.setDate(LocalDate.now());
            // Read drilling hours (as in ExcelToReportLoader)
            String drillHoursStr = excelReader.readCellRangeConcatenated(
                    new ByteArrayInputStream(fileBytes), "BF", "BH", 6, 0);
            try {
                double drillH = (drillHoursStr != null && !drillHoursStr.trim().isEmpty())
                        ? Double.parseDouble(drillHoursStr.trim())
                        : 0.0;
                report.setDrillingHours(drillH);
            } catch (NumberFormatException e) {
                report.setDrillingHours(0.0);
            }

            // Read depth
            String depthStr = excelReader.readCellRangeConcatenated(
                    new ByteArrayInputStream(fileBytes), "U", "Z", 6, 0);
            try {
                double depth = (depthStr != null && !depthStr.trim().isEmpty())
                        ? Double.parseDouble(depthStr.trim())
                        : 0.0;
                report.setDepth(depth);
            } catch (NumberFormatException e) {
                report.setDepth(0.0);
            }

            // Read cost (TVD)
            String costStr = excelReader.readCellRangeConcatenated(
                    new ByteArrayInputStream(fileBytes), "AF", "AK", 6, 0);
            try {
                double cost = (costStr != null && !costStr.trim().isEmpty())
                        ? Double.parseDouble(costStr.trim())
                        : 0.0;
                report.setTvd(cost);
            } catch (NumberFormatException e) {
                report.setTvd(0.0);
            }

            // Read PLANNED OPERATION
            String plannedOpe1 = excelReader.readCellRangeConcatenated(
                    new ByteArrayInputStream(fileBytes), "O", "BC", 59, 0);

            String phase = excelReader.readCellRangeConcatenated(
                    new ByteArrayInputStream(fileBytes), "M", "P", 10, 0);
            report.setPhase(phase);
            String plannedOpe2 = excelReader.readCellRangeConcatenated(
                    new ByteArrayInputStream(fileBytes), "O", "BC", 58, 0);
            String plannedOpe = plannedOpe1 + " " + plannedOpe2;

            report.setPlannedOperation(plannedOpe);

            // Read ACTUAL DAY
            String dayStr = excelReader.readCellRangeConcatenated(
                    new ByteArrayInputStream(fileBytes), "BQ", "BY", 62, 0);
            try {
                double day = (dayStr != null && !dayStr.trim().isEmpty())
                        ? Double.parseDouble(dayStr.trim())
                        : 0.0;
                report.setDay(day);
            } catch (NumberFormatException e) {
                report.setDay(0.0);
            }

            // Read DRILLING PROGRESS
            String drillProgressStr = excelReader.readCellRangeConcatenated(
                    new ByteArrayInputStream(fileBytes), "AW", "BA", 6, 0);
            try {
                double drillProgress = (drillProgressStr != null && !drillProgressStr.trim().isEmpty())
                        ? Double.parseDouble(drillProgressStr.trim())
                        : 0.0;
                report.setDrillingProgress(drillProgress);
            } catch (NumberFormatException e) {
                report.setDrillingProgress(0.0);
            }

            // Save report first to get its ID
            Report savedReport = reportRepository.save(report);

            // Create and add operations (from rows 22 to 43)// Dans

            // PAR CETTE VERSION CORRIGÉE :

            List<String> remarksList = new ArrayList<>();

            for (int row = 46; row <= 56; row++) {
                try {
                    String rowRemark = excelReader.readCellRangeConcatenated(
                            new ByteArrayInputStream(fileBytes), "B", "BG", row, 0);

                    if (rowRemark != null && !rowRemark.trim().isEmpty()) {
                        remarksList.add(rowRemark.trim());
                    }
                } catch (Exception e) {
                    // Log error but continue processing
                    logger.error("Error processing row {}: {}", row, e.getMessage());
                }
            }

            // Set remarks - the entity will handle null/empty cases
            report.setRemarks(remarksList.isEmpty() ? null : remarksList);
            int operationsAdded = addOperationsToReport(fileBytes, savedReport);

            // Add DailyCost
            DailyCost dailyCost = addDailyCostToReport(fileBytes, savedReport);

            // Re-save the report with all its operations
            reportRepository.save(savedReport);

            // Build response with additional debug info
            StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append("Report created successfully! ID: ").append(savedReport.getId())
                    .append(", Date: ").append(savedReport.getDate())
                    .append(", Drilling Hours: ").append(report.getDrillingHours())
                    .append(", Operations added: ").append(operationsAdded)
                    .append(", Planned operations: ").append(plannedOpe)
                    .append(", Day: ").append(dayStr);

            // Add DailyCost info
            if (dailyCost != null) {
                responseBuilder.append("\nDailyCost ID: ").append(dailyCost.getId())
                        .append(", Name: ").append(dailyCost.getName())
                        .append(", DrillingRig: ").append(dailyCost.getDrillingRig())
                        .append(", MudLogging: ").append(dailyCost.getMudLogging());
            } else {
                responseBuilder.append("\nNo DailyCost created.");
            }

            responseBuilder.append(", Puit: ").append(puit.getPuitName())
                    .append(" (ID: ").append(puit.getPuitId()).append(")");
            return ResponseEntity.ok(responseBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        }
    }

    private int addOperationsToReport(byte[] fileBytes, Report report) {
        int operationsAdded = 0;
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

        // Process rows 22 to 43 as in ExcelToReportLoader
        for (int row = 22; row <= 43; row++) {
            try {
                // Read all data first with explicit sheet index
                String code = excelReader.readCellRangeConcatenated(
                        new ByteArrayInputStream(fileBytes), "AW", "AZ", row, 0);
                String startTimeStr = excelReader.readCellRangeConcatenated(
                        new ByteArrayInputStream(fileBytes), "B", "D", row, 0);
                String endTimeStr = excelReader.readCellRangeConcatenated(
                        new ByteArrayInputStream(fileBytes), "F", "H", row, 0);
                String description = excelReader.readCellRangeConcatenated(
                        new ByteArrayInputStream(fileBytes), "I", "AV", row, 0);
                String initialDepthStr = excelReader.readCellRangeConcatenated(
                        new ByteArrayInputStream(fileBytes), "BA", "BE", row, 0);
                String finalDepthStr = excelReader.readCellRangeConcatenated(
                        new ByteArrayInputStream(fileBytes), "BF", "BK", row, 0);
                String rateStr = excelReader.readCellRangeConcatenated(
                        new ByteArrayInputStream(fileBytes), "BR", "BT", row, 0);

                // Skip completely empty rows (no start time)
                if (startTimeStr == null || startTimeStr.trim().isEmpty()) {
                    continue;
                }

                // Create an operation with proper parsing and default values
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
                        continue; // Skip to next row if startTime can't be parsed
                    }
                }
                op.setStartTime(startTime);

                // Parse end time or use start time + 1 hour if missing
                LocalTime endTime;
                if (endTimeStr == null || endTimeStr.trim().isEmpty()) {
                    endTime = startTime.plusHours(1);
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
                        }
                    }
                }
                op.setEndTime(endTime);

                // Set description or default value
                op.setDescription(description != null && !description.trim().isEmpty() ? description.trim()
                        : "Operation at row " + row);

                // Parse numeric values with default values
                double initialDepth = 0.0;
                if (initialDepthStr != null && !initialDepthStr.trim().isEmpty()) {
                    try {
                        initialDepth = Double.parseDouble(initialDepthStr.trim());
                    } catch (NumberFormatException e) {
                        // Use default value
                    }
                }
                op.setInitialDepth(initialDepth);

                double finalDepth = initialDepth; // Default to initial depth
                if (finalDepthStr != null && !finalDepthStr.trim().isEmpty()) {
                    try {
                        finalDepth = Double.parseDouble(finalDepthStr.trim());
                    } catch (NumberFormatException e) {
                        // Use initial depth
                    }
                }
                op.setFinalDepth(finalDepth);

                op.setRate(rateStr);
                op.setDate(report.getDate()); // Use report date instead of LocalDate.now()

                // Add operation to report
                op.setReport(report);
                report.addOperation(op);
                operationsAdded++;

            } catch (Exception e) {
                System.err.println("Error processing operation at row " + row + ": " + e.getMessage());
                // Continue with next row even if there's an error with this one
            }
        }

        return operationsAdded;
    }

    private DailyCost addDailyCostToReport(byte[] fileBytes, Report report) {
        try {
            DailyCost dailyCost = new DailyCost();

            // Set a proper name with the report date
            dailyCost.setName("Daily Cost for " + report.getDate());

            // Log workbook structure for debugging
            int numSheets = 0;
            try (InputStream is = new ByteArrayInputStream(fileBytes)) {
                Workbook workbook = new XSSFWorkbook(is);
                numSheets = workbook.getNumberOfSheets();
                System.out.println("Excel file has " + numSheets + " sheets");
                for (int i = 0; i < numSheets; i++) {
                    System.out.println("Sheet " + i + ": " + workbook.getSheetName(i));
                }
            } catch (Exception e) {
                System.err.println("Error checking workbook structure: " + e.getMessage());
            }

            // Try reading values from both sheet indices
            boolean foundValues = false;

            // First try sheet 1 (index 1) if it exists
            if (numSheets > 1) {
                foundValues = tryReadDailyCostValues(fileBytes, dailyCost, 1);
            }

            // If no values found or only one sheet exists, try sheet 0
            if (!foundValues) {
                foundValues = tryReadDailyCostValues(fileBytes, dailyCost, 0);
            }

            // If still no values found, try alternative columns
            if (!foundValues) {
                // Try sheet 0 with column F
                tryReadDailyCostValues(fileBytes, dailyCost, 0, "F");

                // Try sheet 1 with column F if it exists
                if (numSheets > 1) {
                    tryReadDailyCostValues(fileBytes, dailyCost, 1, "F");
                }
            }

            // Associate DailyCost with Report regardless of values found
            dailyCost.setReport(report);
            report.setDailyCost(dailyCost);

            // Save and return the DailyCost
            DailyCost savedDailyCost = dailyCostRepository.save(dailyCost);

            // Debug info
            System.out.println("DailyCost created with ID: " + savedDailyCost.getId());
            System.out.println("Sample DailyCost values:");
            System.out.println("  Drilling Rig: " + dailyCost.getDrillingRig());
            System.out.println("  Mud Logging: " + dailyCost.getMudLogging());
            System.out.println("  Drilling Mud: " + dailyCost.getDrillingMud());

            return savedDailyCost;

        } catch (Exception e) {
            System.err.println("Error adding DailyCost to report: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tries to read DailyCost values from a specific sheet
     * 
     * @return true if at least one value was successfully read
     */
    private boolean tryReadDailyCostValues(byte[] fileBytes, DailyCost dailyCost, int sheetIndex) {
        return tryReadDailyCostValues(fileBytes, dailyCost, sheetIndex, "G");
    }

    /**
     * Tries to read DailyCost values from a specific sheet and column
     * 
     * @return true if at least one value was successfully read
     */
    private boolean tryReadDailyCostValues(byte[] fileBytes, DailyCost dailyCost, int sheetIndex, String column) {
        System.out.println("Trying to read DailyCost from sheet " + sheetIndex + ", column " + column);

        // Read values for each cost category from the Excel sheet
        // dailyCost.setDrillingRig(evaluateCell(fileBytes, column, 12, sheetIndex));
        // dailyCost.setMudLogging(evaluateCell(fileBytes, column, 17, sheetIndex));
        // dailyCost.setDownwholeTools(evaluateCell(fileBytes, column, 21, sheetIndex));
        // dailyCost.setDrillingMud(evaluateCell(fileBytes, column, 26, sheetIndex));
        // dailyCost.setSolidControl(evaluateCell(fileBytes, column, 31, sheetIndex));
        // dailyCost.setElectricServices(evaluateCell(fileBytes, column, 37,
        // sheetIndex));
        // dailyCost.setBits(evaluateCell(fileBytes, column, 40, sheetIndex));
        // dailyCost.setCasing(evaluateCell(fileBytes, column, 44, sheetIndex));
        // dailyCost.setAccesoriesCasing(evaluateCell(fileBytes, column, 50,
        // sheetIndex));
        // dailyCost.setCasingTubing(evaluateCell(fileBytes, column, 54, sheetIndex));
        // dailyCost.setCementing(evaluateCell(fileBytes, column, 59, sheetIndex));
        // dailyCost.setRigSupervision(evaluateCell(fileBytes, column, 65, sheetIndex));
        // dailyCost.setCommunications(evaluateCell(fileBytes, column, 72, sheetIndex));
        // dailyCost.setWaterSupply(evaluateCell(fileBytes, column, 73, sheetIndex));
        // dailyCost.setWaterServices(evaluateCell(fileBytes, column, 79, sheetIndex));
        // dailyCost.setSecurity(evaluateCell(fileBytes, column, 83, sheetIndex));

        // Read values for each cost category from the Excel sheet
        dailyCost.setDrillingRig(evaluateCell(fileBytes, column, 12, sheetIndex));
        dailyCost.setMudLogging(evaluateCell(fileBytes, column, 17, sheetIndex));
        dailyCost.setDownwholeTools(evaluateCell(fileBytes, column, 21, sheetIndex));
        dailyCost.setDrillingMud(evaluateCell(fileBytes, column, 26, sheetIndex));
        dailyCost.setSolidControl(evaluateCell(fileBytes, column, 31, sheetIndex));
        dailyCost.setElectricServices(evaluateCell(fileBytes, column, 37, sheetIndex));
        dailyCost.setBits(evaluateCell(fileBytes, column, 43, sheetIndex));
        dailyCost.setCasing(evaluateCell(fileBytes, column, 46, sheetIndex));
        dailyCost.setAccesoriesCasing(evaluateCell(fileBytes, column, 51, sheetIndex));
        dailyCost.setCasingTubing(evaluateCell(fileBytes, column, 55, sheetIndex));
        dailyCost.setCementing(evaluateCell(fileBytes, column, 63, sheetIndex));
        dailyCost.setRigSupervision(evaluateCell(fileBytes, column, 68, sheetIndex));
        dailyCost.setCommunications(evaluateCell(fileBytes, column, 73, sheetIndex));
        dailyCost.setWaterSupply(evaluateCell(fileBytes, column, 77, sheetIndex));
        dailyCost.setWaterServices(evaluateCell(fileBytes, column, 80, sheetIndex));
        dailyCost.setSecurity(evaluateCell(fileBytes, column, 84, sheetIndex));
        dailyCost.setDailyCost(evaluateCell(fileBytes, column, 86, sheetIndex));
        // Check if at least one value was set properly
        boolean hasAtLeastOneValue = dailyCost.getDrillingRig() > 0 || dailyCost.getMudLogging() > 0 ||
                dailyCost.getDownwholeTools() > 0 || dailyCost.getDrillingMud() > 0 ||
                dailyCost.getSolidControl() > 0 || dailyCost.getElectricServices() > 0 ||
                dailyCost.getBits() > 0 || dailyCost.getCasing() > 0 ||
                dailyCost.getAccesoriesCasing() > 0 || dailyCost.getCasingTubing() > 0 ||
                dailyCost.getCementing() > 0 || dailyCost.getRigSupervision() > 0 ||
                dailyCost.getCommunications() > 0 || dailyCost.getWaterSupply() > 0 ||
                dailyCost.getWaterServices() > 0 || dailyCost.getSecurity() > 0;

        if (hasAtLeastOneValue) {
            System.out.println("Successfully read DailyCost values from sheet " + sheetIndex + ", column " + column);
        } else {
            System.out.println("No values found on sheet " + sheetIndex + ", column " + column);
        }

        return hasAtLeastOneValue;
    }

    /**
     * Evaluates a cell's value, handling formulas properly
     */
    public double evaluateCell(byte[] fileBytes, String column, int rowIndex, int sheetIndex) {
        try (InputStream is = new ByteArrayInputStream(fileBytes);
                Workbook workbook = new XSSFWorkbook(is)) {

            // Get the sheet
            if (sheetIndex < 0 || sheetIndex >= workbook.getNumberOfSheets()) {
                return 0.0;
            }
            Sheet sheet = workbook.getSheetAt(sheetIndex);

            // Get the row
            Row row = sheet.getRow(rowIndex - 1); // Excel rows are 0-based in POI
            if (row == null) {
                return 0.0;
            }

            // Get the cell
            int columnIndex = excelReader.excelColumnToIndex(column);
            Cell cell = row.getCell(columnIndex);
            if (cell == null) {
                return 0.0;
            }

            // Handle different cell types
            switch (cell.getCellType()) {
                case NUMERIC:
                    return cell.getNumericCellValue();
                case FORMULA:
                    try {
                        // Create a formula evaluator
                        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                        // Evaluate the formula
                        CellValue cellValue = evaluator.evaluate(cell);

                        // Return the numeric result if available
                        if (cellValue.getCellType() == CellType.NUMERIC) {
                            return cellValue.getNumberValue();
                        } else {
                            System.out.println("Formula evaluated to non-numeric result for cell " + column + rowIndex);
                            return 0.0;
                        }
                    } catch (Exception e) {
                        System.err.println(
                                "Error evaluating formula in cell " + column + rowIndex + ": " + e.getMessage());
                        // Log the formula that caused the issue
                        System.err.println("Formula was: " + cell.getCellFormula());
                        return 0.0;
                    }
                case STRING:
                    String stringValue = cell.getStringCellValue().trim();
                    // Try to parse as double
                    try {
                        // Handle potential commas as decimal separators
                        stringValue = stringValue.replace(',', '.');
                        return Double.parseDouble(stringValue);
                    } catch (NumberFormatException e) {
                        System.err.println("Could not parse string value as number: " + stringValue);
                        return 0.0;
                    }
                default:
                    return 0.0;
            }
        } catch (Exception e) {
            System.err.println("Exception evaluating cell " + column + rowIndex + ": " + e.getMessage());
            return 0.0;
        }
    }

    @PostMapping("/extract")
    public ResponseEntity<Map<String, Object>> extractReportData(@RequestParam("file") MultipartFile file,
            @RequestParam("puitId") String puitId) {
        try {

            Puit puit = puitRepository.findById(puitId)
                    .orElseThrow(() -> new RuntimeException("Puit non trouvé avec l'ID: " + puitId));

            byte[] fileBytes = file.getBytes();

            // Extraire toutes les données sans sauvegarder
            Map<String, Object> extractedData = new HashMap<>();

            // Données du rapport principal
            String drillHoursStr = excelReader.readCellRangeConcatenated(
                    new ByteArrayInputStream(fileBytes), "BF", "BH", 6, 0);
            double drillH = parseDouble(drillHoursStr, 0.0);

            String depthStr = excelReader.readCellRangeConcatenated(
                    new ByteArrayInputStream(fileBytes), "U", "Z", 6, 0);
            double depth = parseDouble(depthStr, 0.0);

            String costStr = excelReader.readCellRangeConcatenated(
                    new ByteArrayInputStream(fileBytes), "AF", "AK", 6, 0);
            double cost = parseDouble(costStr, 0.0);

            String phase = excelReader.readCellRangeConcatenated(
                    new ByteArrayInputStream(fileBytes), "M", "P", 10, 0);

            String plannedOpe1 = excelReader.readCellRangeConcatenated(
                    new ByteArrayInputStream(fileBytes), "O", "BC", 59, 0);
            String plannedOpe2 = excelReader.readCellRangeConcatenated(
                    new ByteArrayInputStream(fileBytes), "O", "BC", 58, 0);
            String plannedOpe = plannedOpe1 + " " + plannedOpe2;

            String dayStr = excelReader.readCellRangeConcatenated(
                    new ByteArrayInputStream(fileBytes), "BQ", "BY", 62, 0);
            double day = parseDouble(dayStr, 0.0);

            String drillProgressStr = excelReader.readCellRangeConcatenated(
                    new ByteArrayInputStream(fileBytes), "AW", "BA", 6, 0);
            double drillProgress = parseDouble(drillProgressStr, 0.0);

            // Données du rapport
            Map<String, Object> reportData = new HashMap<>();
            reportData.put("drillingHours", drillH);
            reportData.put("depth", depth);
            reportData.put("tvd", cost);
            reportData.put("phase", phase);
            reportData.put("plannedOperation", plannedOpe);
            reportData.put("day", day);
            reportData.put("drillingProgress", drillProgress);
            extractedData.put("puitId", puitId); // AJOUTER L'ID DU PUIT
            extractedData.put("puitName", puit.getPuitName()); // AJOUTER LE NOM DU PUIT

            // Extraire les opérations
            List<Map<String, Object>> operations = extractOperations(fileBytes);

            // Extraire les coûts quotidiens
            Map<String, Object> dailyCostData = extractDailyCostEx(fileBytes);
            // hilaw

            // Extraire les remarques
            List<String> remarks = extractRemarks(fileBytes);

            extractedData.put("report", reportData);
            extractedData.put("operations", operations);
            extractedData.put("dailyCost", dailyCostData);
            extractedData.put("remarks", remarks);
            extractedData.put("fileData", Base64.getEncoder().encodeToString(fileBytes)); // Pour la sauvegarde
                                                                                          // ultérieure

            return ResponseEntity.ok(extractedData);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'extraction: " + e.getMessage()));
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmAndSaveReport(@RequestBody Map<String, Object> confirmData) {
        try {
            // Debug: Afficher les données reçues
            logger.info("Données reçues pour confirmation: {}", confirmData);

            // Récupérer l'ID du puit depuis les données confirmées
            String puitId = (String) confirmData.get("puitId");
            if (puitId == null || puitId.trim().isEmpty()) {
                logger.error("ID du puit manquant dans les données: {}", confirmData.keySet());
                return ResponseEntity.badRequest().body("ID du puit manquant");
            }

            // Vérifier que le puit existe
            Puit puit = puitRepository.findById(puitId)
                    .orElseThrow(() -> new RuntimeException("Puit non trouvé avec l'ID: " + puitId));

            String fileDataBase64 = (String) confirmData.get("fileData");
            if (fileDataBase64 == null || fileDataBase64.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Données du fichier manquantes");
            }

            byte[] fileBytes = Base64.getDecoder().decode(fileDataBase64);

            // Créer le rapport avec les données modifiées
            Report report = new Report();
            report.setPuit(puit); // ASSOCIER LE PUIT
            report.setExcelFile(fileBytes);
            report.setDate(LocalDate.now());

            // Utiliser les données modifiées du frontend avec vérifications
            Map<String, Object> reportData = (Map<String, Object>) confirmData.get("report");
            if (reportData == null) {
                return ResponseEntity.badRequest().body("Données du rapport manquantes");
            }

            // Debug: Afficher les données du rapport
            logger.info("Données du rapport: {}", reportData);

            // Utiliser des conversions sécurisées
            report.setDrillingHours(safeDoubleValue(reportData.get("drillingHours")));
            report.setDepth(safeDoubleValue(reportData.get("depth")));
            report.setTvd(safeDoubleValue(reportData.get("tvd")));
            report.setPhase(safeStringValue(reportData.get("phase")));
            report.setPlannedOperation(safeStringValue(reportData.get("plannedOperation")));
            report.setDay(safeDoubleValue(reportData.get("day")));
            report.setDrillingProgress(safeDoubleValue(reportData.get("drillingProgress")));

            // Debug: Vérifier les valeurs assignées
            logger.info("Valeurs assignées au rapport - DrillingHours: {}, Depth: {}, TVD: {}",
                    report.getDrillingHours(), report.getDepth(), report.getTvd());

            // Remarques
            List<String> remarks = (List<String>) confirmData.get("remarks");
            report.setRemarks(remarks);

            // Sauvegarder le rapport
            Report savedReport = reportRepository.save(report);
            logger.info("Rapport sauvegardé avec ID: {}", savedReport.getId());

            // Sauvegarder les opérations modifiées
            List<Map<String, Object>> operationsData = (List<Map<String, Object>>) confirmData.get("operations");
            if (operationsData != null) {
                int operationsSaved = saveOperations(operationsData, savedReport);
                logger.info("Nombre d'opérations sauvegardées: {}", operationsSaved);
            }

            // Sauvegarder les coûts quotidiens modifiés
            Map<String, Object> dailyCostData = (Map<String, Object>) confirmData.get("dailyCost");
            if (dailyCostData != null) {
                DailyCost savedDailyCost = saveDailyCost(dailyCostData, savedReport);
                logger.info("DailyCost sauvegardé avec ID: {}",
                        savedDailyCost != null ? savedDailyCost.getId() : "null");
            }

            return ResponseEntity.ok("Rapport sauvegardé avec succès! ID: " + savedReport.getId());

        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }

    // Méthodes utilitaires
    private double parseDouble(String value, double defaultValue) {
        try {
            return (value != null && !value.trim().isEmpty()) ? Double.parseDouble(value.trim()) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private List<Map<String, Object>> extractOperations(byte[] fileBytes) {
        List<Map<String, Object>> operations = new ArrayList<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

        for (int row = 22; row <= 43; row++) {
            try {
                String startTimeStr = excelReader.readCellRangeConcatenated(
                        new ByteArrayInputStream(fileBytes), "B", "D", row, 0);

                if (startTimeStr == null || startTimeStr.trim().isEmpty()) {
                    continue;
                }

                Map<String, Object> opData = new HashMap<>();
                opData.put("code", excelReader.readCellRangeConcatenated(
                        new ByteArrayInputStream(fileBytes), "AW", "AZ", row, 0));
                opData.put("startTime", startTimeStr.trim());
                opData.put("endTime", excelReader.readCellRangeConcatenated(
                        new ByteArrayInputStream(fileBytes), "F", "H", row, 0));
                opData.put("description", excelReader.readCellRangeConcatenated(
                        new ByteArrayInputStream(fileBytes), "I", "AV", row, 0));
                opData.put("initialDepth", parseDouble(excelReader.readCellRangeConcatenated(
                        new ByteArrayInputStream(fileBytes), "BA", "BE", row, 0), 0.0));
                opData.put("finalDepth", parseDouble(excelReader.readCellRangeConcatenated(
                        new ByteArrayInputStream(fileBytes), "BF", "BK", row, 0), 0.0));
                opData.put("rate", excelReader.readCellRangeConcatenated(
                        new ByteArrayInputStream(fileBytes), "BR", "BT", row, 0));

                operations.add(opData);
            } catch (Exception e) {
                logger.error("Erreur extraction opération ligne {}: {}", row, e.getMessage());
            }
        }
        return operations;
    }

    private Map<String, Object> extractDailyCostEx(byte[] fileBytes) {
        Map<String, Object> costData = new HashMap<>();

        // Initialiser toutes les valeurs à 0.0 par défaut
        costData.put("drillingRig", 0.0);
        costData.put("mudLogging", 0.0);
        costData.put("downwholeTools", 0.0);
        costData.put("drillingMud", 0.0);
        costData.put("solidControl", 0.0);
        costData.put("electricServices", 0.0);
        costData.put("bits", 0.0);
        costData.put("casing", 0.0);
        costData.put("accesoriesCasing", 0.0);
        costData.put("casingTubing", 0.0);
        costData.put("cementing", 0.0);
        costData.put("rigSupervision", 0.0);
        costData.put("communications", 0.0);
        costData.put("waterSupply", 0.0);
        costData.put("waterServices", 0.0);
        costData.put("security", 0.0);
        costData.put("dailyCost", 0.0);

        String column = "G"; // Colonne G uniquement
        Integer sheetIndex = 1; // Feuille 1 uniquement

        try {
            logger.info("Lecture des données sur feuille {} colonne {}", sheetIndex, column);

            // Lire TOUS les champs pour cette combinaison colonne/feuille
            Map<String, Object> tempCostData = new HashMap<>();

            tempCostData.put("drillingRig", evaluateCell(fileBytes, column, 12, sheetIndex));
            tempCostData.put("mudLogging", evaluateCell(fileBytes, column, 17, sheetIndex));
            tempCostData.put("downwholeTools", evaluateCell(fileBytes, column, 21, sheetIndex));
            tempCostData.put("drillingMud", evaluateCell(fileBytes, column, 26, sheetIndex));
            tempCostData.put("solidControl", evaluateCell(fileBytes, column, 31, sheetIndex));
            tempCostData.put("electricServices", evaluateCell(fileBytes, column, 37, sheetIndex));
            tempCostData.put("bits", evaluateCell(fileBytes, column, 43, sheetIndex));
            tempCostData.put("casing", evaluateCell(fileBytes, column, 46, sheetIndex));
            tempCostData.put("accesoriesCasing", evaluateCell(fileBytes, column, 51, sheetIndex));
            tempCostData.put("casingTubing", evaluateCell(fileBytes, column, 55, sheetIndex));
            tempCostData.put("cementing", evaluateCell(fileBytes, column, 63, sheetIndex));
            tempCostData.put("rigSupervision", evaluateCell(fileBytes, column, 68, sheetIndex));
            tempCostData.put("communications", evaluateCell(fileBytes, column, 73, sheetIndex));
            tempCostData.put("waterSupply", evaluateCell(fileBytes, column, 77, sheetIndex));
            tempCostData.put("waterServices", evaluateCell(fileBytes, column, 80, sheetIndex));
            tempCostData.put("security", evaluateCell(fileBytes, column, 84, sheetIndex));
            tempCostData.put("dailyCost", evaluateCell(fileBytes, column, 86, sheetIndex));

            // Vérifier si cette combinaison a donné des résultats
            boolean hasValidData = tempCostData.values().stream()
                    .anyMatch(value -> value != null && ((Double) value) > 0.0);

            if (hasValidData) {
                logger.info("Données valides trouvées sur feuille {} colonne {}", sheetIndex, column);

                // Afficher les valeurs trouvées pour debug
                tempCostData.forEach((key, value) -> {
                    if (value != null && ((Double) value) > 0.0) {
                        logger.info("  {}: {}", key, value);
                    }
                });

                // Copier toutes les valeurs trouvées
                costData.putAll(tempCostData);
            } else {
                logger.warn("Aucune donnée valide trouvée sur feuille {} colonne {}", sheetIndex, column);
            }

        } catch (Exception e) {
            logger.error("Erreur lors de la lecture des données: {}", e.getMessage());
        }

        return costData;
    }

    private Map<String, Object> extractDailyCost(byte[] fileBytes) {
        Map<String, Object> costData = new HashMap<>();

        // Initialiser toutes les valeurs à 0.0 par défaut
        costData.put("drillingRig", 0.0);
        costData.put("mudLogging", 0.0);
        costData.put("downwholeTools", 0.0);
        costData.put("drillingMud", 0.0);
        costData.put("solidControl", 0.0);
        costData.put("electricServices", 0.0);
        costData.put("bits", 0.0);
        costData.put("casing", 0.0);
        costData.put("accesoriesCasing", 0.0);
        costData.put("casingTubing", 0.0);
        costData.put("cementing", 0.0);
        costData.put("rigSupervision", 0.0);
        costData.put("communications", 0.0);
        costData.put("waterSupply", 0.0);
        costData.put("waterServices", 0.0);
        costData.put("security", 0.0);
        costData.put("dailyCost", 0.0);

        try (InputStream is = new ByteArrayInputStream(fileBytes);
                Workbook workbook = new XSSFWorkbook(is)) {

            // Essayer chaque feuille
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                logger.info("Tentative d'extraction des coûts depuis la feuille {}", sheetIndex);

                // Essayer différentes colonnes
                for (String column : new String[] { "G", "F", "E" }) {
                    logger.info("Essai colonne {} sur feuille {}", column, sheetIndex);

                    // Lire TOUS les champs pour cette combinaison colonne/feuille
                    Map<String, Object> tempCostData = new HashMap<>();

                    tempCostData.put("drillingRig", evaluateCell(fileBytes, column, 12, sheetIndex));
                    tempCostData.put("mudLogging", evaluateCell(fileBytes, column, 17, sheetIndex));
                    tempCostData.put("downwholeTools", evaluateCell(fileBytes, column, 21, sheetIndex));
                    tempCostData.put("drillingMud", evaluateCell(fileBytes, column, 26, sheetIndex));
                    tempCostData.put("solidControl", evaluateCell(fileBytes, column, 31, sheetIndex));
                    tempCostData.put("electricServices", evaluateCell(fileBytes, column, 37, sheetIndex));
                    tempCostData.put("bits", evaluateCell(fileBytes, column, 43, sheetIndex));
                    tempCostData.put("casing", evaluateCell(fileBytes, column, 46, sheetIndex));
                    tempCostData.put("accesoriesCasing", evaluateCell(fileBytes, column, 51, sheetIndex));
                    tempCostData.put("casingTubing", evaluateCell(fileBytes, column, 55, sheetIndex));
                    tempCostData.put("cementing", evaluateCell(fileBytes, column, 63, sheetIndex));
                    tempCostData.put("rigSupervision", evaluateCell(fileBytes, column, 68, sheetIndex));
                    tempCostData.put("communications", evaluateCell(fileBytes, column, 73, sheetIndex));
                    tempCostData.put("waterSupply", evaluateCell(fileBytes, column, 77, sheetIndex));
                    tempCostData.put("waterServices", evaluateCell(fileBytes, column, 80, sheetIndex));
                    tempCostData.put("security", evaluateCell(fileBytes, column, 84, sheetIndex));
                    tempCostData.put("dailyCost", evaluateCell(fileBytes, column, 86, sheetIndex));

                    // Vérifier si cette combinaison a donné des résultats
                    boolean hasValidData = tempCostData.values().stream()
                            .anyMatch(value -> ((Double) value) > 0.0);

                    if (hasValidData) {
                        logger.info("Données valides trouvées sur feuille {} colonne {}", sheetIndex, column);

                        // Afficher les valeurs trouvées pour debug
                        tempCostData.forEach((key, value) -> {
                            if (((Double) value) > 0.0) {
                                logger.info("  {}: {}", key, value);
                            }
                        });

                        // Copier toutes les valeurs trouvées
                        costData.putAll(tempCostData);
                        return costData; // Retourner avec TOUTES les données de cette combinaison
                    }
                }
            }

            logger.warn("Aucune donnée de coût valide trouvée dans le fichier Excel");

        } catch (Exception e) {
            logger.error("Erreur lors de l'extraction des coûts", e);
        }

        return costData;
    }

    private List<String> extractRemarks(byte[] fileBytes) {
        List<String> remarksList = new ArrayList<>();
        for (int row = 46; row <= 56; row++) {
            try {
                String rowRemark = excelReader.readCellRangeConcatenated(
                        new ByteArrayInputStream(fileBytes), "B", "BG", row, 0);
                if (rowRemark != null && !rowRemark.trim().isEmpty()) {
                    remarksList.add(rowRemark.trim());
                }
            } catch (Exception e) {
                logger.error("Erreur extraction remarque ligne {}: {}", row, e.getMessage());
            }
        }
        return remarksList;
    }

    private DailyCost saveDailyCost(Map<String, Object> dailyCostData, Report report) {
        try {
            logger.info("Sauvegarde DailyCost avec données: {}", dailyCostData);

            DailyCost dailyCost = new DailyCost();
            dailyCost.setName("Daily Cost for " + report.getDate());

            // Utiliser une méthode helper pour convertir les valeurs de manière sécurisée
            dailyCost.setDrillingRig(safeDoubleValue(dailyCostData.get("drillingRig")));
            dailyCost.setMudLogging(safeDoubleValue(dailyCostData.get("mudLogging")));
            dailyCost.setDownwholeTools(safeDoubleValue(dailyCostData.get("downwholeTools")));
            dailyCost.setDrillingMud(safeDoubleValue(dailyCostData.get("drillingMud")));
            dailyCost.setSolidControl(safeDoubleValue(dailyCostData.get("solidControl")));
            dailyCost.setElectricServices(safeDoubleValue(dailyCostData.get("electricServices")));
            dailyCost.setBits(safeDoubleValue(dailyCostData.get("bits")));
            dailyCost.setCasing(safeDoubleValue(dailyCostData.get("casing")));
            dailyCost.setAccesoriesCasing(safeDoubleValue(dailyCostData.get("accesoriesCasing")));
            dailyCost.setCasingTubing(safeDoubleValue(dailyCostData.get("casingTubing")));
            dailyCost.setCementing(safeDoubleValue(dailyCostData.get("cementing")));
            dailyCost.setRigSupervision(safeDoubleValue(dailyCostData.get("rigSupervision")));
            dailyCost.setCommunications(safeDoubleValue(dailyCostData.get("communications")));
            dailyCost.setWaterSupply(safeDoubleValue(dailyCostData.get("waterSupply")));
            dailyCost.setWaterServices(safeDoubleValue(dailyCostData.get("waterServices")));
            dailyCost.setSecurity(safeDoubleValue(dailyCostData.get("security")));
            dailyCost.setDailyCost(safeDoubleValue(dailyCostData.get("dailyCost")));

            dailyCost.setReport(report);
            report.setDailyCost(dailyCost); // Important: Associer dans les deux sens

            DailyCost savedDailyCost = dailyCostRepository.save(dailyCost);
            logger.info("DailyCost sauvegardé - ID: {}, DrillingRig: {}, MudLogging: {}",
                    savedDailyCost.getId(), savedDailyCost.getDrillingRig(), savedDailyCost.getMudLogging());

            return savedDailyCost;
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde du DailyCost: ", e);
            return null;
        }
    }

    // Méthode helper pour convertir de manière sécurisée
    private double safeDoubleValue(Object value) {
        if (value == null) {
            logger.debug("Valeur null reçue pour conversion en double");
            return 0.0;
        }

        if (value instanceof Number) {
            double result = ((Number) value).doubleValue();
            logger.debug("Conversion Number vers double: {} -> {}", value, result);
            return result;
        }

        if (value instanceof String) {
            try {
                String stringValue = ((String) value).trim();
                if (stringValue.isEmpty()) {
                    logger.debug("String vide reçue pour conversion en double");
                    return 0.0;
                }
                // Gérer les virgules comme séparateurs décimaux
                stringValue = stringValue.replace(',', '.');
                double result = Double.parseDouble(stringValue);
                logger.debug("Conversion String vers double: '{}' -> {}", value, result);
                return result;
            } catch (NumberFormatException e) {
                logger.warn("Impossible de convertir la valeur string en double: '{}'", value);
                return 0.0;
            }
        }

        logger.warn("Type de valeur non supporté pour la conversion en double: {} (type: {})",
                value, value.getClass().getSimpleName());
        return 0.0;
    }

    // Version sécurisée de saveOperations
    private int saveOperations(List<Map<String, Object>> operationsData, Report report) {
        int savedCount = 0;
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

        for (int i = 0; i < operationsData.size(); i++) {
            Map<String, Object> opData = operationsData.get(i);
            try {
                logger.debug("Sauvegarde opération {}: {}", i, opData);

                Operation op = new Operation();

                // Gérer le code de manière sécurisée
                String code = safeStringValue(opData.get("code"));
                op.setCode(code.isEmpty() ? "OP-" + (i + 1) : code);

                // Parse times
                String startTimeStr = safeStringValue(opData.get("startTime"));
                if (startTimeStr.isEmpty()) {
                    logger.warn("StartTime manquant pour l'opération {}, ignorée", i);
                    continue;
                }

                op.setStartTime(LocalTime.parse(startTimeStr));

                String endTimeStr = safeStringValue(opData.get("endTime"));
                if (!endTimeStr.isEmpty()) {
                    if (endTimeStr.equals("24:00")) {
                        op.setEndTime(LocalTime.of(0, 0));
                    } else {
                        op.setEndTime(LocalTime.parse(endTimeStr));
                    }
                } else {
                    op.setEndTime(op.getStartTime().plusHours(1));
                }

                // Gérer la description de manière sécurisée
                op.setDescription(safeStringValue(opData.get("description")));

                // Utiliser safeDoubleValue pour les valeurs numériques
                op.setInitialDepth(safeDoubleValue(opData.get("initialDepth")));
                op.setFinalDepth(safeDoubleValue(opData.get("finalDepth")));

                // Gérer le rate de manière sécurisée
                op.setRate(safeStringValue(opData.get("rate")));

                op.setDate(report.getDate());
                op.setReport(report);

                Operation savedOp = operationRepository.save(op);
                logger.debug("Opération sauvegardée avec ID: {}", savedOp.getId());
                savedCount++;

            } catch (Exception e) {
                logger.error("Erreur lors de la sauvegarde de l'opération {}: {}", i, e.getMessage());
                // Continuer avec l'opération suivante
            }
        }
        return savedCount;
    }

    // Méthode helper pour les String
    private String safeStringValue(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString().trim();
    }

}