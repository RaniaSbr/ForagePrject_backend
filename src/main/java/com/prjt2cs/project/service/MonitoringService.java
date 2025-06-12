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

    // Version optimisée de l'état global par phase
    public List<PhaseStatus> getEtatParPhase() {
        List<PhaseStatus> result = new ArrayList<>();
        List<Report> allReports = reportRepository.findAllWithEssentialRelations();

        for (PhasePrevision prevision : previsionRepository.findAll()) {
            String phaseName = prevision.getPhaseName();

            // Rapports associés à la phase
            List<Report> reportsForPhase = allReports.stream()
                    .filter(r -> r.getPhase() != null && normalize(r.getPhase()).equals(normalize(phaseName)))
                    .collect(Collectors.toList());

            // Somme des coûts réels
            double coutReel = reportsForPhase.stream()
                    .map(Report::getDailyCost)
                    .filter(Objects::nonNull)
                    .mapToDouble(dc -> dc.getDailyCost() != null ? dc.getDailyCost() : 0.0)
                    .sum();

            // Calcul du délai réel
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
                    delaiReel));
        }

        return result;
    }

    // Version optimisée pour un puits spécifique
    public List<PhaseStatus> getEtatParPhaseParPuits(String puitId) {
        List<PhaseStatus> result = new ArrayList<>();

        // Récupération optimisée des rapports pour ce puits
        List<Report> reportsForWell = reportRepository.findByPuitIdWithDailyCost(puitId);

        for (PhasePrevision prevision : previsionRepository.findAll()) {
            String phaseName = prevision.getPhaseName();

            // Rapports associés à la phase pour ce puits
            List<Report> reportsForPhase = reportsForWell.stream()
                    .filter(r -> r.getPhase() != null && normalize(r.getPhase()).equals(normalize(phaseName)))
                    .collect(Collectors.toList());

            // Somme des coûts réels pour cette phase et ce puits
            double coutReel = reportsForPhase.stream()
                    .map(Report::getDailyCost)
                    .filter(Objects::nonNull)
                    .mapToDouble(dc -> dc.getDailyCost() != null ? dc.getDailyCost() : 0.0)
                    .sum();

            // Calcul du délai réel pour cette phase et ce puits
            int delaiReel = reportsForPhase.stream()
                    .map(Report::getDay)
                    .filter(Objects::nonNull)
                    .mapToInt(day -> Math.max(1, day.intValue()))
                    .max()
                    .orElse(0);

            result.add(new PhaseStatus(
                    phaseName,
                    prevision.getTotal() != null ? prevision.getTotal() : 0.0,
                    coutReel,
                    prevision.getNombreJours() != null ? prevision.getNombreJours() : 0,
                    delaiReel));
        }

        return result;
    }

    // Version optimisée pour tous les puits
    public Map<String, List<PhaseStatus>> getEtatParPhasePourTousLesPuits() {
        Map<String, List<PhaseStatus>> result = new HashMap<>();

        // Récupération optimisée des IDs de puits
        List<String> puitIds = reportRepository.findDistinctPuitIds();

        // Pour chaque puits, calculer l'état par phase
        for (String puitId : puitIds) {
            result.put(puitId, getEtatParPhaseParPuits(puitId));
        }

        return result;
    }

    // Méthode optimisée pour obtenir les puits avec leurs détails
    public List<Map<String, Object>> getAvailableWellsWithDetails() {
        return reportRepository.findDistinctPuitIds().stream()
                .map(puitId -> {
                    // Récupérer les informations du puits via un rapport
                    Optional<Report> sampleReport = reportRepository.findByPuitIdWithDailyCost(puitId).stream()
                            .findFirst();

                    Map<String, Object> puitInfo = new HashMap<>();
                    if (sampleReport.isPresent() && sampleReport.get().getPuit() != null) {
                        Puit puit = sampleReport.get().getPuit();
                        puitInfo.put("puitId", puit.getPuitId());
                        puitInfo.put("puitName", puit.getPuitName());
                        puitInfo.put("location", puit.getLocation());
                        puitInfo.put("status", puit.getStatus());
                        puitInfo.put("totalDepth", puit.getTotalDepth());
                        puitInfo.put("nombreReports", puit.getNombreReports());
                    } else {
                        puitInfo.put("puitId", puitId);
                        puitInfo.put("puitName", "N/A");
                        puitInfo.put("location", "N/A");
                        puitInfo.put("status", "N/A");
                        puitInfo.put("totalDepth", 0.0);
                        puitInfo.put("nombreReports", 0);
                    }
                    return puitInfo;
                })
                .collect(Collectors.toList());
    }

    // Méthode simple pour obtenir les IDs de puits
    public List<String> getAvailableWells() {
        return reportRepository.findDistinctPuitIds();
    }

    // Méthode pour obtenir un résumé par puits
    public Map<String, Map<String, Object>> getResumePuits() {
        Map<String, Map<String, Object>> resume = new HashMap<>();
        List<String> puitIds = reportRepository.findDistinctPuitIds();

        for (String puitId : puitIds) {
            List<Report> reports = reportRepository.findByPuitIdWithDailyCost(puitId);

            Map<String, Object> puitResume = new HashMap<>();

            // Informations générales du puits
            if (!reports.isEmpty() && reports.get(0).getPuit() != null) {
                Puit puit = reports.get(0).getPuit();
                puitResume.put("puitName", puit.getPuitName());
                puitResume.put("location", puit.getLocation());
                puitResume.put("status", puit.getStatus());
                puitResume.put("totalDepth", puit.getTotalDepth());
            }

            // Statistiques des rapports
            puitResume.put("nombreReports", reports.size());

            // Coût total
            double coutTotal = reports.stream()
                    .map(Report::getDailyCost)
                    .filter(Objects::nonNull)
                    .mapToDouble(dc -> dc.getDailyCost() != null ? dc.getDailyCost() : 0.0)
                    .sum();
            puitResume.put("coutTotal", coutTotal);

            // Progression maximale
            OptionalDouble maxDepth = reports.stream()
                    .mapToDouble(r -> r.getDepth() != null ? r.getDepth() : 0.0)
                    .max();
            puitResume.put("profondeurActuelle", maxDepth.orElse(0.0));

            // Phases couvertes
            Set<String> phases = reports.stream()
                    .map(Report::getPhase)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            puitResume.put("phases", phases);

            resume.put(puitId, puitResume);
        }

        return resume;
    }

    private String normalize(String value) {
        return value.replaceAll("[\\s\"'']", "").toLowerCase();
    }
}