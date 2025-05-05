package com.prjt2cs.project.controller;

import com.prjt2cs.project.model.Operation;
import com.prjt2cs.project.model.Report;
import com.prjt2cs.project.repository.OperationRepository;
import com.prjt2cs.project.repository.ReportRepository;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class HelloController {

    private final ReportRepository reportRepository;
    private final OperationRepository operationRepository;

    public HelloController(ReportRepository reportRepository, OperationRepository operationRepository) {
        this.reportRepository = reportRepository;
        this.operationRepository = operationRepository;
    }

    // Endpoint pour obtenir tous les rapports
    @GetMapping("/reports")
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    // Endpoint pour obtenir un rapport spécifique par ID
    @GetMapping("/reports/{id}")
    public Report getReportById(@PathVariable Long id) {
        Optional<Report> report = reportRepository.findById(id);
        return report.orElse(null);
    }

    // Endpoint pour obtenir toutes les opérations
    @GetMapping("/operations")
    public List<Operation> getAllOperations() {
        return operationRepository.findAll();
    }

    // Endpoint pour obtenir les opérations d'un rapport spécifique
    @GetMapping("/reports/{id}/operations")
    public List<Operation> getOperationsByReportId(@PathVariable Long id) {
        Optional<Report> report = reportRepository.findById(id);
        if (report.isPresent()) {
            return report.get().getOperations();
        }
        return List.of(); // Retourne une liste vide si le rapport n'existe pas
    }
}