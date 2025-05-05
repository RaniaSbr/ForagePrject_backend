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
    public String readCellRangeConcatenated(String fileName, String startColumn, String endColumn,
            int rowIndex, int sheetIndex) {
        StringBuilder concatenatedValues = new StringBuilder();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("excel/" + fileName)) {
            if (is == null) {
                System.out.println("Fichier introuvable !");
                return "";
            }

            Workbook workbook = new XSSFWorkbook(is);

            // Vérifier si l'index de la feuille est valide
            if (sheetIndex < 0 || sheetIndex >= workbook.getNumberOfSheets()) {
                System.out.println("Index de feuille invalide : " + sheetIndex);
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
    public String readCellRangeConcatenated(String fileName, String startColumn, String endColumn, int rowIndex) {
        return readCellRangeConcatenated(fileName, startColumn, endColumn, rowIndex, 0);
    }

    // Méthodes pour vérifier et afficher les valeurs

    public void verifyAndPrintCellsConcatenated(String fileName, String startColumn, String endColumn,
            int rowIndex, int sheetIndex) {
        System.out.println("Lecture du fichier Excel : " + fileName + ", feuille #" + sheetIndex);

        String result = readCellRangeConcatenated(fileName, startColumn, endColumn, rowIndex, sheetIndex);

        if (result.isEmpty()) {
            System.out.println("Aucune donnée trouvée dans la plage de cellules spécifiée.");
        } else {
            System.out.println("Valeurs concaténées : " + result);
        }
    }

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

    public void verifyAndPrintCellsConcatenated(String fileName, String startColumn, String endColumn, int rowIndex) {
        verifyAndPrintCellsConcatenated(fileName, startColumn, endColumn, rowIndex, 0);
    }

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
}