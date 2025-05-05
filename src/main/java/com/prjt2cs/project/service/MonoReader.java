package com.prjt2cs.project.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.IOException;

@Service
public class MonoReader {

    // Convertit une lettre de colonne Excel ("A", "AB", etc.) en index (0-based)
    public int excelColumnToIndex(String column) {
        int index = 0;
        for (int i = 0; i < column.length(); i++) {
            index *= 26;
            index += column.charAt(i) - 'A' + 1;
        }
        return index - 1;
    }

    // Lit une cellule classique en tant que chaîne
    public String readCellAsString(String fileName, String column, int rowIndex, int sheetIndex) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("excel/" + fileName)) {
            if (is == null) {
                System.out.println("Fichier introuvable !");
                return "";
            }

            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(sheetIndex);

            int colIndex = excelColumnToIndex(column);
            Row row = sheet.getRow(rowIndex - 1); // 0-based
            if (row != null) {
                Cell cell = row.getCell(colIndex);
                if (cell != null) {
                    workbook.close();
                    return cell.toString();
                }
            }

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    // 💡 Nouvelle méthode : lit une cellule avec formule et retourne la valeur
    // calculée (double)
    public double readCalculatedCellAsDouble(String fileName, String column, int rowIndex, int sheetIndex) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("excel/" + fileName)) {
            if (is == null) {
                System.out.println("Fichier introuvable !");
                return 0.0;
            }

            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            int colIndex = excelColumnToIndex(column);
            Row row = sheet.getRow(rowIndex - 1); // 0-based
            if (row != null) {
                Cell cell = row.getCell(colIndex);
                if (cell != null) {
                    CellValue evaluated = evaluator.evaluate(cell);
                    if (evaluated != null && evaluated.getCellType() == CellType.NUMERIC) {
                        double result = evaluated.getNumberValue();
                        workbook.close();
                        return result;
                    }
                }
            }

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0.0;
    }
}
