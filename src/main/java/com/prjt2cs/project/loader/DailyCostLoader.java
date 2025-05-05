package com.prjt2cs.project.loader;

import com.prjt2cs.project.model.DailyCost;
import com.prjt2cs.project.model.Report;
import com.prjt2cs.project.repository.DailyCostRepository;
import com.prjt2cs.project.repository.ReportRepository;
import com.prjt2cs.project.service.ExcelReader;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DailyCostLoader {

    private final ExcelReader excelReader;
    private final DailyCostRepository dailyCostRepository;
    private final ReportRepository reportRepository;

    public DailyCostLoader(ExcelReader excelReader,
            DailyCostRepository dailyCostRepository,
            ReportRepository reportRepository) {
        this.excelReader = excelReader;
        this.dailyCostRepository = dailyCostRepository;
        this.reportRepository = reportRepository;
    }

    /**
     * Importe les données de coût quotidien à partir d'un fichier Excel et les
     * associe à un rapport
     * 
     * @param fileName   Nom du fichier Excel
     * @param sheetIndex Index de la feuille Excel (la deuxième feuille correspond à
     *                   l'index 1)
     * @param reportId   ID du rapport auquel associer les coûts
     * @return DailyCost objet créé et sauvegardé
     */
    public DailyCost importDailyCostFromExcel(String fileName, int sheetIndex, Long reportId) {
        // Vérifier que le rapport existe
        Optional<Report> reportOpt = reportRepository.findById(reportId);
        if (!reportOpt.isPresent()) {
            throw new IllegalArgumentException("Report with ID " + reportId + " not found");
        }

        Report report = reportOpt.get();

        // Créer un nouvel objet DailyCost
        DailyCost dailyCost = new DailyCost();
        dailyCost.setName("Daily Cost for " + report.getDate());

        // Lire les valeurs de chaque coût depuis la feuille Excel
        // Supposons que les valeurs sont dans la colonne Col pour chaque ligne

        // En supposant que les données sont organisées avec les noms dans une colonne
        // et les valeurs dans une autre
        // Ajuster les numéros de ligne en fonction de la structure réelle de votre

        // Excel
        String Col = "G";
        dailyCost.setDrillingRig(readDoubleValue(fileName, Col, Col, 12, sheetIndex));
        dailyCost.setMudLogging(readDoubleValue(fileName, Col, Col, 17, sheetIndex));
        dailyCost.setDownwholeTools(readDoubleValue(fileName, Col, Col, 21, sheetIndex));
        dailyCost.setDrillingMud(readDoubleValue(fileName, Col, Col, 26, sheetIndex));
        dailyCost.setSolidControl(readDoubleValue(fileName, Col, Col, 31, sheetIndex));
        dailyCost.setElectricServices(readDoubleValue(fileName, Col, Col, 37, sheetIndex));
        dailyCost.setBits(readDoubleValue(fileName, Col, Col, 40, sheetIndex));
        dailyCost.setCasing(readDoubleValue(fileName, Col, Col, 44, sheetIndex));
        dailyCost.setAccesoriesCasing(readDoubleValue(fileName, Col, Col, 50, sheetIndex));
        dailyCost.setCasingTubing(readDoubleValue(fileName, Col, Col, 54, sheetIndex));
        dailyCost.setCementing(readDoubleValue(fileName, Col, Col, 59, sheetIndex));
        dailyCost.setRigSupervision(readDoubleValue(fileName, Col, Col, 65, sheetIndex));
        dailyCost.setCommunications(readDoubleValue(fileName, Col, Col, 72, sheetIndex));
        dailyCost.setWaterSupply(readDoubleValue(fileName, Col, Col, 73, sheetIndex));
        dailyCost.setWaterServices(readDoubleValue(fileName, Col, Col, 79, sheetIndex));
        dailyCost.setSecurity(readDoubleValue(fileName, Col, Col, 83, sheetIndex));

        // Calculer le coût quotidien total (somme de tous les coûts)
        dailyCost.setCommunications(readDoubleValue(fileName, Col, Col, 85, sheetIndex));

        // Associer le DailyCost au Report
        dailyCost.setReport(report);
        report.setDailyCost(dailyCost);

        // Sauvegarder dans la base de données
        return dailyCostRepository.save(dailyCost);
    }

    /**
     * Importe les données à partir d'un fichier Excel en utilisant le nom de la
     * feuille
     */
    public DailyCost importDailyCostFromExcel(String fileName, String sheetName, Long reportId) {
        // On peut utiliser cette méthode si on préfère accéder à la feuille par son nom
        // Logique similaire à la méthode précédente

        // Vérifier que le rapport existe
        Optional<Report> reportOpt = reportRepository.findById(reportId);
        if (!reportOpt.isPresent()) {
            throw new IllegalArgumentException("Report with ID " + reportId + " not found");
        }

        Report report = reportOpt.get();

        // Créer un nouvel objet DailyCost
        DailyCost dailyCost = new DailyCost();
        dailyCost.setName("Daily Cost for " + report.getDate());

        String Col = "G";

        // Lire les valeurs de chaque coût depuis la feuille Excel spécifiée par son nom
        dailyCost.setDrillingRig(readDoubleValue(fileName, Col, Col, 5, sheetName));
        dailyCost.setMudLogging(readDoubleValue(fileName, Col, Col, 6, sheetName));
        dailyCost.setDownwholeTools(readDoubleValue(fileName, Col, Col, 7, sheetName));
        dailyCost.setDrillingMud(readDoubleValue(fileName, Col, Col, 8, sheetName));
        dailyCost.setSolidControl(readDoubleValue(fileName, Col, Col, 9, sheetName));
        dailyCost.setElectricServices(readDoubleValue(fileName, Col, Col, 10, sheetName));
        dailyCost.setBits(readDoubleValue(fileName, Col, Col, 11, sheetName));
        dailyCost.setCasing(readDoubleValue(fileName, Col, Col, 12, sheetName));
        dailyCost.setAccesoriesCasing(readDoubleValue(fileName, Col, Col, 13, sheetName));
        dailyCost.setCasingTubing(readDoubleValue(fileName, Col, Col, 14, sheetName));
        dailyCost.setCementing(readDoubleValue(fileName, Col, Col, 15, sheetName));
        dailyCost.setRigSupervision(readDoubleValue(fileName, Col, Col, 16, sheetName));
        dailyCost.setCommunications(readDoubleValue(fileName, Col, Col, 17, sheetName));
        dailyCost.setWaterSupply(readDoubleValue(fileName, Col, Col, 18, sheetName));
        dailyCost.setWaterServices(readDoubleValue(fileName, Col, Col, 19, sheetName));
        dailyCost.setSecurity(readDoubleValue(fileName, Col, Col, 20, sheetName));

        dailyCost.setCommunications(readDoubleValue(fileName, Col, Col, 85, sheetName));

        // Associer le DailyCost au Report
        dailyCost.setReport(report);
        report.setDailyCost(dailyCost);

        // Sauvegarder dans la base de données
        return dailyCostRepository.save(dailyCost);
    }

    /**
     * Lit une valeur d'une cellule Excel et la convertit en Double
     */
    private Double readDoubleValue(String fileName, String startColumn, String endColumn, int rowIndex,
            int sheetIndex) {
        String value = excelReader.readCellRangeConcatenated(fileName, startColumn, endColumn, rowIndex, sheetIndex);
        if (value == null || value.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            // Si la conversion échoue, renvoyer 0.0
            return 0.0;
        }
    }

    /**
     * Lit une valeur d'une cellule Excel et la convertit en Double (version avec
     * nom de feuille)
     */
    private Double readDoubleValue(String fileName, String startColumn, String endColumn, int rowIndex,
            String sheetName) {
        String value = excelReader.readCellRangeConcatenated(fileName, startColumn, endColumn, rowIndex, sheetName);
        if (value == null || value.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            // Si la conversion échoue, renvoyer 0.0
            return 0.0;
        }
    }
}