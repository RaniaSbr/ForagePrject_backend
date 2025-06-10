package com.prjt2cs.project.controller;

import com.prjt2cs.project.model.DailyCost;
import com.prjt2cs.project.model.Operation;
import com.prjt2cs.project.model.Report;
import com.prjt2cs.project.repository.ReportRepository;
import com.prjt2cs.project.repository.DailyCostRepository;
import com.prjt2cs.project.repository.OperationRepository;
import com.prjt2cs.project.service.ExcelReader;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportUploadController {

    private static final Logger logger = LoggerFactory.getLogger(ReportUploadController.class);

    private final ReportRepository reportRepository;
    private final DailyCostRepository dailyCostRepository;
    private final ExcelReader excelReader;

    public ReportUploadController(
            ReportRepository reportRepository,
            DailyCostRepository dailyCostRepository,
            OperationRepository operationRepository,
            ExcelReader excelReader) {
        this.dailyCostRepository = dailyCostRepository;
        this.reportRepository = reportRepository;
        this.excelReader = excelReader;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> createReportFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            // Copy file content to memory

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
                try (Workbook workbook = new XSSFWorkbook(is)) {
                    numSheets = workbook.getNumberOfSheets();
                    System.out.println("Excel file has " + numSheets + " sheets");
                    for (int i = 0; i < numSheets; i++) {
                        System.out.println("Sheet " + i + ": " + workbook.getSheetName(i));
                    }
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
}