package com.prjt2cs.project.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExcelReader {

    public int excelColumnToIndex(String column) {
        int index = 0;
        for (int i = 0; i < column.length(); i++) {
            index *= 26;
            index += column.charAt(i) - 'A' + 1;
        }
        return index - 1;
    }

    private String getExcelColumnName(int index) {
        StringBuilder columnName = new StringBuilder();
        while (index >= 0) {
            int remainder = index % 26;
            columnName.insert(0, (char) (remainder + 'A'));
            index = (index / 26) - 1;
        }
        return columnName.toString();
    }

    public Map<String, String> readCellRange(String fileName, String startColumn, String endColumn, int rowIndex) {
        Map<String, String> cellData = new HashMap<>();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("excel/" + fileName)) {
            if (is == null) {
                System.out.println("Fichier introuvable !");
                return cellData;
            }

            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);

            int startCol = excelColumnToIndex(startColumn);
            int endCol = excelColumnToIndex(endColumn);

            Row row = sheet.getRow(rowIndex - 1); // Java index starts at 0
            if (row != null) {
                for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    String value = (cell != null) ? cell.toString() : "";
                    cellData.put(getExcelColumnName(colIndex) + rowIndex, value);
                }
            }

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cellData;
    }
}
