package com.prjt2cs.hello;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.io.IOException;

public class ExcelReader {

    // Convertir une lettre Excel (ex : "CS") en index Java
    public int excelColumnToIndex(String column) {
        int index = 0;
        for (int i = 0; i < column.length(); i++) {
            index *= 26;
            index += column.charAt(i) - 'A' + 1;
        }
        return index - 1;
    }

    // Convertir index Java en nom de colonne Excel
    private String getExcelColumnName(int index) {
        StringBuilder columnName = new StringBuilder();
        while (index >= 0) {
            int remainder = index % 26;
            columnName.insert(0, (char) (remainder + 'A'));
            index = (index / 26) - 1;
        }
        return columnName.toString();
    }

    // Lire CS29 à DD29
    public void readCellRangeCS_DD(String fileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("excel/" + fileName)) {
            if (is == null) {
                System.out.println("Fichier introuvable dans le dossier resources/excel");
                return;
            }

            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);

            int rowIndex = 23; // Ligne 29
            int startCol = excelColumnToIndex("BA");
            int endCol = excelColumnToIndex("BA");

            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    String value = (cell != null) ? cell.toString() : "Cellule vide";
                    System.out.println("Cellule " + getExcelColumnName(colIndex) + (rowIndex + 1) + " = " + value);
                }
            } else {
                System.out.println("Ligne " + (rowIndex + 1) + " non trouvée !");
            }

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
