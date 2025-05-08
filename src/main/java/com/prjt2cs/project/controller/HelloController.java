package com.prjt2cs.project.controller;

import com.prjt2cs.project.model.DailyCost;
import com.prjt2cs.project.model.Operation;
import com.prjt2cs.project.model.Report;
import com.prjt2cs.project.repository.DailyCostRepository;
import com.prjt2cs.project.repository.OperationRepository;
import com.prjt2cs.project.repository.ReportRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class HelloController {

    private final ReportRepository reportRepository;
    private final OperationRepository operationRepository;
    private final DailyCostRepository dailyCostRepository;

    public HelloController(ReportRepository reportRepository,
            OperationRepository operationRepository,
            DailyCostRepository dailyCostRepository) {
        this.reportRepository = reportRepository;
        this.operationRepository = operationRepository;
        this.dailyCostRepository = dailyCostRepository;
    }

    // Endpoint to get all reports
    @GetMapping("/reports")
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    // Endpoint to get a specific report by ID
    @GetMapping("/reports/{id}")
    public Report getReportById(@PathVariable Long id) {
        Optional<Report> report = reportRepository.findById(id);
        return report.orElse(null);
    }

    // Endpoint to get all operations
    @GetMapping("/operations")
    public List<Operation> getAllOperations() {
        return operationRepository.findAll();
    }

    // Endpoint to get operations of a specific report
    @GetMapping("/reports/{id}/operations")
    public List<Operation> getOperationsByReportId(@PathVariable Long id) {
        Optional<Report> report = reportRepository.findById(id);
        if (report.isPresent()) {
            return report.get().getOperations();
        }
        return List.of(); // Returns an empty list if the report doesn't exist
    }

    // Endpoint to get all daily costs
    @GetMapping("/dailycosts")
    public List<DailyCost> getAllDailyCosts() {
        return dailyCostRepository.findAll();
    }

    // Endpoint to get a specific daily cost by ID
    @GetMapping("/dailycosts/{id}")
    public DailyCost getDailyCostById(@PathVariable Long id) {
        Optional<DailyCost> dailyCost = dailyCostRepository.findById(id);
        return dailyCost.orElse(null);
    }

    // Endpoint to get daily cost of a specific report
    @GetMapping("/reports/{id}/dailycost")
    public DailyCost getDailyCostByReportId(@PathVariable Long id) {
        Optional<Report> report = reportRepository.findById(id);
        if (report.isPresent() && report.get().getDailyCost() != null) {
            return report.get().getDailyCost();
        }
        return null; // Returns null if the report doesn't exist or has no daily cost
    }

        // Endpoint pour déclarer/modifier les anomalies **************************************************************
        @PatchMapping("/reports/{id}/anomalies")
        public ResponseEntity<Report> declareAnomalies(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
            
            String anomaliesDescription = payload.get("anomalies");
        
            Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapport non trouvé"));
        
            report.setAnomalies(anomaliesDescription);
            reportRepository.save(report);
        
            return ResponseEntity.ok(report);
        }


    @PatchMapping("/reports/{id}/review")
    public ResponseEntity<Report> submitReview(
        @PathVariable Long id,
        @RequestBody Map<String, String> payload) {
            
        String expertAnalysis = payload.get("expertAnalysis");
        String expertRecommendations = payload.get("expertRecommendations");

        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rapport introuvable"));

        report.setAnalysis(expertAnalysis);
        report.setRecommendations(expertRecommendations);
        reportRepository.save(report);

        return ResponseEntity.ok(report);
    }

    //***************************************************** */
}  