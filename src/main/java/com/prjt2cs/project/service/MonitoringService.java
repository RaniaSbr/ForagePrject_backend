package com.prjt2cs.project.service;

import com.prjt2cs.project.dto.PhaseStatus;
import com.prjt2cs.project.model.*;
import com.prjt2cs.project.repository.*;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MonitoringService {

    private final PhasePrevisionRepository previsionRepository;
    private final ReportRepository reportRepository;

    public MonitoringService(PhasePrevisionRepository previsionRepository, ReportRepository reportRepository) {
        this.previsionRepository = previsionRepository;
        this.reportRepository = reportRepository;
    }

    public List<PhaseStatus> getEtatParPhase() {
        List<PhaseStatus> result = new ArrayList<>();
        List<Report> allReports = reportRepository.findAll();

        for (PhasePrevision prevision : previsionRepository.findAll()) {
            String phaseName = prevision.getPhaseName();

            // Rapports associés à la phase, normalisation pour éviter les erreurs de guillemets ou espaces
            List<Report> reportsForPhase = allReports.stream()
                    .filter(r -> r.getPhase() != null && normalize(r.getPhase()).equals(normalize(phaseName)))
                    .collect(Collectors.toList());

            // Somme des coûts réels (dailyCost.dailyCost)
            double coutReel = reportsForPhase.stream()
                    .map(Report::getDailyCost)
                    .filter(Objects::nonNull)
                    .mapToDouble(dc -> dc.getDailyCost() != null ? dc.getDailyCost() : 0.0)
                    .sum();

            // Calcul du délai réel (minimum 1 même si le jour est 0)
            int delaiReel = reportsForPhase.stream()
                    .map(Report::getDay)
                    .filter(Objects::nonNull)
                    .mapToInt(day -> Math.max(1, day.intValue()))
                    .max()
                    .orElse(1);

            result.add(new PhaseStatus(
                    phaseName,
                    prevision.getTotal() != null ? prevision.getTotal() : 0.0,
                    coutReel,
                    prevision.getNombreJours() != null ? prevision.getNombreJours() : 0,
                    delaiReel
            ));
        }

        return result;
    }

    private String normalize(String value) {
        return value.replaceAll("[\\s\"’']", "").toLowerCase();
    }
}
