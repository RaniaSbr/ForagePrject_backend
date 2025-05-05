package com.prjt2cs.project.loader;

import com.prjt2cs.project.model.DailyCost;
import com.prjt2cs.project.model.Report;
import com.prjt2cs.project.repository.DailyCostRepository;
import com.prjt2cs.project.repository.ReportRepository;
import com.prjt2cs.project.service.MonoReader;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DailyCostLoader {

    private final MonoReader monoReader;
    private final DailyCostRepository dailyCostRepository;
    private final ReportRepository reportRepository;

    public DailyCostLoader(MonoReader monoReader,
            DailyCostRepository dailyCostRepository,
            ReportRepository reportRepository) {
        this.monoReader = monoReader;
        this.dailyCostRepository = dailyCostRepository;
        this.reportRepository = reportRepository;
    }

    public DailyCost importDailyCostFromExcel(String fileName, int sheetIndex, Long reportId) {
        System.out.println("Attempting to read daily costs from file: " + fileName + ", sheet: " + sheetIndex);

        Optional<Report> reportOpt = reportRepository.findById(reportId);
        if (!reportOpt.isPresent()) {
            throw new IllegalArgumentException("Report with ID " + reportId + " not found");
        }

        Report report = reportOpt.get();

        DailyCost dailyCost = new DailyCost();
        dailyCost.setName("Daily Cost for " + report.getDate());

        String col = "G";
        // Use readCalculatedCellAsDouble instead of readDoubleValue
        dailyCost.setDrillingRig(monoReader.readCalculatedCellAsDouble(fileName, col, 12, sheetIndex));
        System.out.println("DrillingRig: " + dailyCost.getDrillingRig());

        dailyCost.setMudLogging(monoReader.readCalculatedCellAsDouble(fileName, col, 17, sheetIndex));
        System.out.println("MudLogging: " + dailyCost.getMudLogging());

        dailyCost.setDownwholeTools(monoReader.readCalculatedCellAsDouble(fileName, col, 21, sheetIndex));
        System.out.println("DownwholeTools: " + dailyCost.getDownwholeTools());

        dailyCost.setDrillingMud(monoReader.readCalculatedCellAsDouble(fileName, col, 26, sheetIndex));
        dailyCost.setSolidControl(monoReader.readCalculatedCellAsDouble(fileName, col, 31, sheetIndex));
        dailyCost.setElectricServices(monoReader.readCalculatedCellAsDouble(fileName, col, 37, sheetIndex));
        dailyCost.setBits(monoReader.readCalculatedCellAsDouble(fileName, col, 40, sheetIndex));
        dailyCost.setCasing(monoReader.readCalculatedCellAsDouble(fileName, col, 44, sheetIndex));
        dailyCost.setAccesoriesCasing(monoReader.readCalculatedCellAsDouble(fileName, col, 50, sheetIndex));
        dailyCost.setCasingTubing(monoReader.readCalculatedCellAsDouble(fileName, col, 54, sheetIndex));
        dailyCost.setCementing(monoReader.readCalculatedCellAsDouble(fileName, col, 59, sheetIndex));
        dailyCost.setRigSupervision(monoReader.readCalculatedCellAsDouble(fileName, col, 65, sheetIndex));
        dailyCost.setCommunications(monoReader.readCalculatedCellAsDouble(fileName, col, 72, sheetIndex));
        dailyCost.setWaterSupply(monoReader.readCalculatedCellAsDouble(fileName, col, 73, sheetIndex));
        dailyCost.setWaterServices(monoReader.readCalculatedCellAsDouble(fileName, col, 79, sheetIndex));
        dailyCost.setSecurity(monoReader.readCalculatedCellAsDouble(fileName, col, 83, sheetIndex));

        // Read the total daily cost from G85 with formula evaluation
        dailyCost.setDailyCost(monoReader.readCalculatedCellAsDouble(fileName, col, 85, sheetIndex));
        System.out.println("Total Daily Cost from cell G85: " + dailyCost.getDailyCost());

        // Set bidirectional relationship
        dailyCost.setReport(report);
        report.setDailyCost(dailyCost);

        // Save and return the dailyCost
        DailyCost savedDailyCost = dailyCostRepository.save(dailyCost);
        System.out.println("Daily cost saved with ID: " + savedDailyCost.getId() +
                ", Total cost: " + savedDailyCost.getDailyCost());

        return savedDailyCost;
    }
}