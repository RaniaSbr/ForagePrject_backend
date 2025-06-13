package com.prjt2cs.project.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.IOException;

@Service
public class ExcelReader {

    public int excelColumnToIndex(String column) {
        int index = 0;
        for (int i = 0; i < column.length(); i++) {
            index *= 26;
            index += column.charAt(i) - 'A' + 1;
        }
        return index - 1;
    }

    /**
     * Lit une plage de cellules et retourne les valeurs concaténées.
     * Permet de spécifier la feuille à utiliser par son index (commence à 0).
     */
    // public String readCellRangeConcatenated(String fileName, String startColumn,
    // String endColumn,
    // int rowIndex, int sheetIndex) {
    // StringBuilder concatenatedValues = new StringBuilder();

    // try (InputStream is =
    // getClass().getClassLoader().getResourceAsStream("excel/" + fileName)) {
    // if (is == null) {
    // System.out.println("Fichier introuvable !");
    // return "";
    // }

    // Workbook workbook = new XSSFWorkbook(is);

    // // Vérifier si l'index de la feuille est valide
    // if (sheetIndex < 0 || sheetIndex >= workbook.getNumberOfSheets()) {
    // System.out.println("Index de feuille invalide : " + sheetIndex);
    // return "";
    // }

    // Sheet sheet = workbook.getSheetAt(sheetIndex);

    // int startCol = excelColumnToIndex(startColumn);
    // int endCol = excelColumnToIndex(endColumn);

    // Row row = sheet.getRow(rowIndex - 1);
    // if (row != null) {
    // for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
    // Cell cell = row.getCell(colIndex);
    // String value = (cell != null) ? cell.toString().trim() : "";

    // // Concaténer les valeurs dans la chaîne
    // if (!value.isEmpty()) {
    // concatenatedValues.append(value).append(" "); // Ajoute un espace entre les
    // valeurs
    // }
    // }
    // }

    // workbook.close();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }

    // // Retirer l'espace final ajouté à la fin de la concaténation
    // return concatenatedValues.toString().trim();
    // }

    /**
     * An improved version of readSingleCell that handles different cell types
     * properly
     */
    public double readSingleCellAsDouble(InputStream inputStream, String columnLetter, int rowIndex, int sheetIndex) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            if (sheetIndex < 0 || sheetIndex >= workbook.getNumberOfSheets()) {
                System.out.println("Invalid sheet index: " + sheetIndex);
                return Double.NaN;
            }

            Sheet sheet = workbook.getSheetAt(sheetIndex);
            int columnIndex = excelColumnToIndex(columnLetter);
            Row row = sheet.getRow(rowIndex - 1); // Rows are 0-indexed in POI

            if (row == null) {
                System.out.println("Row " + rowIndex + " does not exist in sheet " + sheetIndex);
                return Double.NaN;
            }

            Cell cell = row.getCell(columnIndex);
            if (cell == null) {
                System.out.println("Cell at column " + columnLetter + ", row " + rowIndex + " does not exist");
                return Double.NaN;
            }

            // Handle numeric and formula cells that result in numbers
            return switch (cell.getCellType()) {
                case NUMERIC -> cell.getNumericCellValue();
                case FORMULA -> {
                    FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                    CellValue cellValue = evaluator.evaluate(cell);
                    yield switch (cellValue.getCellType()) {
                        case NUMERIC -> cellValue.getNumberValue();
                        default -> {
                            System.out.println("Formula does not evaluate to a number");
                            yield Double.NaN;
                        }
                    };
                }
                default -> {
                    System.out.println("Cell is not numeric or numeric formula");
                    yield Double.NaN;
                }
            };
        } catch (IOException e) {
            System.err.println("Error reading Excel cell: " + e.getMessage());
            e.printStackTrace();
            return Double.NaN;
        }
    }

    public String readCellRangeConcatenated(InputStream inputStream, String startColumn, String endColumn,
            int rowIndex, int sheetIndex) {
        StringBuilder concatenatedValues = new StringBuilder();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {

            if (sheetIndex < 0 || sheetIndex >= workbook.getNumberOfSheets()) {
                System.out.println("Invalid sheet index: " + sheetIndex);
                return "";
            }

            Sheet sheet = workbook.getSheetAt(sheetIndex);

            int startCol = excelColumnToIndex(startColumn);
            int endCol = excelColumnToIndex(endColumn);

            Row row = sheet.getRow(rowIndex - 1);
            if (row != null) {
                for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    String value = (cell != null) ? cell.toString().trim() : "";

                    if (!value.isEmpty()) {
                        concatenatedValues.append(value).append(" ");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return concatenatedValues.toString().trim();
    }

    /**
     * Surcharge permettant de spécifier la feuille par son nom.
     */
    public String readCellRangeConcatenated(String fileName, String startColumn, String endColumn,
            int rowIndex, String sheetName) {
        StringBuilder concatenatedValues = new StringBuilder();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("excel/" + fileName)) {
            if (is == null) {
                System.out.println("Fichier introuvable !");
                return "";
            }

            Workbook workbook = new XSSFWorkbook(is);

            // Récupérer la feuille par son nom
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                System.out.println("Feuille non trouvée : " + sheetName);
                return "";
            }

            int startCol = excelColumnToIndex(startColumn);
            int endCol = excelColumnToIndex(endColumn);

            Row row = sheet.getRow(rowIndex - 1);
            if (row != null) {
                for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    String value = (cell != null) ? cell.toString().trim() : "";

                    // Concaténer les valeurs dans la chaîne
                    if (!value.isEmpty()) {
                        concatenatedValues.append(value).append(" "); // Ajoute un espace entre les valeurs
                    }
                }
            }

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Retirer l'espace final ajouté à la fin de la concaténation
        return concatenatedValues.toString().trim();
    }

    /**
     * Version par défaut qui utilise la première feuille (pour compatibilité avec
     * le code existant)
     */
    // public String readCellRangeConcatenated(String fileName, String startColumn,
    // String endColumn, int rowIndex) {
    // return readCellRangeConcatenated(fileName, startColumn, endColumn, rowIndex,
    // 0);
    // }

    // // Méthodes pour vérifier et afficher les valeurs

    // public void verifyAndPrintCellsConcatenated(String fileName, String
    // startColumn, String endColumn,
    // int rowIndex, int sheetIndex) {
    // System.out.println("Lecture du fichier Excel : " + fileName + ", feuille #" +
    // sheetIndex);

    // String result = readCellRangeConcatenated(fileName, startColumn, endColumn,
    // rowIndex, sheetIndex);

    // if (result.isEmpty()) {
    // System.out.println("Aucune donnée trouvée dans la plage de cellules
    // spécifiée.");
    // } else {
    // System.out.println("Valeurs concaténées : " + result);
    // }
    // }

    public void verifyAndPrintCellsConcatenated(String fileName, String startColumn, String endColumn,
            int rowIndex, String sheetName) {
        System.out.println("Lecture du fichier Excel : " + fileName + ", feuille '" + sheetName + "'");

        String result = readCellRangeConcatenated(fileName, startColumn, endColumn, rowIndex, sheetName);

        if (result.isEmpty()) {
            System.out.println("Aucune donnée trouvée dans la plage de cellules spécifiée.");
        } else {
            System.out.println("Valeurs concaténées : " + result);
        }
    }

    // public void verifyAndPrintCellsConcatenated(String fileName, String
    // startColumn, String endColumn, int rowIndex) {
    // verifyAndPrintCellsConcatenated(fileName, startColumn, endColumn, rowIndex,
    // 0);
    // }

    /**
     * Méthode utilitaire pour lister toutes les feuilles d'un fichier Excel
     */
    public void listSheets(String fileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("excel/" + fileName)) {
            if (is == null) {
                System.out.println("Fichier introuvable !");
                return;
            }

            Workbook workbook = new XSSFWorkbook(is);
            int sheetCount = workbook.getNumberOfSheets();

            System.out.println("Le fichier " + fileName + " contient " + sheetCount + " feuille(s):");
            for (int i = 0; i < sheetCount; i++) {
                System.out.println("  " + i + ": " + workbook.getSheetName(i));
            }

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readCell(InputStream inputStream, String columnLetter, int rowIndex, int sheetIndex) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            if (sheetIndex < 0 || sheetIndex >= workbook.getNumberOfSheets()) {
                throw new IllegalArgumentException("Index de feuille invalide : " + sheetIndex);
            }

            Sheet sheet = workbook.getSheetAt(sheetIndex);
            int columnIndex = excelColumnToIndex(columnLetter);
            Row row = sheet.getRow(rowIndex - 1);

            if (row != null) {
                Cell cell = row.getCell(columnIndex);
                if (cell != null) {
                    return getCellValueAsString(cell);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur de lecture cellule [" + columnLetter + rowIndex + "] : " + e.getMessage());
        }
        return null;
    }

    private String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue().toString()
                    : Double.toString(cell.getNumericCellValue());
            case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue());
            case FORMULA -> {
                FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(cell);
                yield switch (cellValue.getCellType()) {
                    case STRING -> cellValue.getStringValue();
                    case NUMERIC -> Double.toString(cellValue.getNumberValue());
                    case BOOLEAN -> Boolean.toString(cellValue.getBooleanValue());
                    default -> "";
                };
            }
            case BLANK, _NONE -> "";
            default -> throw new IllegalArgumentException("Unexpected value: " + cell.getCellType());
        };
    }

}
