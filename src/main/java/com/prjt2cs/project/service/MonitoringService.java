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

            // Somme des coûts réels (total)
            double coutReel = sumDailyCostAttribute(reportsForPhase, DailyCost::getDailyCost);

            // Calculate actual detailed costs
            double drillingActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getDrilling);
            double mudLoggingActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getMudLogging);
            double cementingActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getCementing);
            double waterSupplyActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getWaterSupply);
            double drillingMudActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getDrillingMud);
            double accesoriesCasingActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getAccesoriesCasing);
            double casingTubingActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getCasingTubing);
            double securityActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getSecurity);
            double bitsActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getBits);

            // Retrieve or calculate prevision values for detailed costs
            // **** IMPORTANT: REPLACE THIS LOGIC WITH YOUR ACTUAL DATA SOURCE FOR PREVISION
            // VALUES ****
            double totalPhasePrevision = prevision.getTotal() != null ? prevision.getTotal() : 0.0;
            // For demonstration, distributing total prevision evenly (you need real values)
            double drillingPrevu = prevision.getDrilling() != 2003 ? prevision.getDrilling()
                    : totalPhasePrevision / 9.0;
            double mudLoggingPrevu = prevision.getMudLogging() != 2003 ? prevision.getMudLogging()
                    : totalPhasePrevision / 9.0;
            double cementingPrevu = prevision.getCementing() != 2003 ? prevision.getCementing()
                    : totalPhasePrevision / 9.0;
            double waterSupplyPrevu = prevision.getWaterSupply() != 2003 ? prevision.getWaterSupply()
                    : totalPhasePrevision / 9.0;
            double drillingMudPrevu = prevision.getDrillingMud() != 2003 ? prevision.getDrillingMud()
                    : totalPhasePrevision / 9.0;
            double accesoriesCasingPrevu = totalPhasePrevision > 0 ? totalPhasePrevision / 9.0 : 0.0;
            double casingTubingPrevu = prevision.getCasingTubing() != 2003 ? prevision.getCasingTubing()
                    : totalPhasePrevision / 9.0;
            double securityPrevu = totalPhasePrevision > 0 ? totalPhasePrevision / 9.0 : 0.0;
            double bitsPrevu = totalPhasePrevision > 0 ? totalPhasePrevision / 9.0 : 0.0;
            // ******************************************************************************************

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
                    delaiReel,
                    drillingActual, mudLoggingActual, cementingActual,
                    waterSupplyActual, drillingMudActual, accesoriesCasingActual,
                    casingTubingActual, securityActual, bitsActual,
                    // Pass prevision values for detailed costs
                    drillingPrevu, mudLoggingPrevu, cementingPrevu,
                    waterSupplyPrevu, drillingMudPrevu, accesoriesCasingPrevu,
                    casingTubingPrevu, securityPrevu, bitsPrevu));
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

            // Somme des coûts réels pour cette phase et ce puits (total)
            double coutReel = sumDailyCostAttribute(reportsForPhase, DailyCost::getDailyCost);

            // Calculate actual detailed costs for this phase and well
            double drillingActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getDrilling);
            double mudLoggingActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getMudLogging);
            double cementingActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getCementing);
            double waterSupplyActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getWaterSupply);
            double drillingMudActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getDrillingMud);
            double accesoriesCasingActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getAccesoriesCasing);
            double casingTubingActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getCasingTubing);
            double securityActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getSecurity);
            double bitsActual = sumDailyCostAttribute(reportsForPhase, DailyCost::getBits);

            // Retrieve or calculate prevision values for detailed costs
            // **** IMPORTANT: REPLACE THIS LOGIC WITH YOUR ACTUAL DATA SOURCE FOR PREVISION
            // VALUES ****
            double totalPhasePrevision = prevision.getTotal() != null ? prevision.getTotal() : 0.0;
            // For demonstration, distributing total prevision evenly (you need real values)
            double drillingPrevu = totalPhasePrevision > 0 ? totalPhasePrevision / 9.0 : 0.0;
            double mudLoggingPrevu = totalPhasePrevision > 0 ? totalPhasePrevision / 9.0 : 0.0;
            double cementingPrevu = totalPhasePrevision > 0 ? totalPhasePrevision / 9.0 : 0.0;
            double waterSupplyPrevu = totalPhasePrevision > 0 ? totalPhasePrevision / 9.0 : 0.0;
            double drillingMudPrevu = totalPhasePrevision > 0 ? totalPhasePrevision / 9.0 : 0.0;
            double accesoriesCasingPrevu = totalPhasePrevision > 0 ? totalPhasePrevision / 9.0 : 0.0;
            double casingTubingPrevu = totalPhasePrevision > 0 ? totalPhasePrevision / 9.0 : 0.0;
            double securityPrevu = totalPhasePrevision > 0 ? totalPhasePrevision / 9.0 : 0.0;
            double bitsPrevu = totalPhasePrevision > 0 ? totalPhasePrevision / 9.0 : 0.0;
            // ******************************************************************************************

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
                    delaiReel,
                    drillingActual, mudLoggingActual, cementingActual,
                    waterSupplyActual, drillingMudActual, accesoriesCasingActual,
                    casingTubingActual, securityActual, bitsActual,
                    // Pass prevision values for detailed costs
                    drillingPrevu, mudLoggingPrevu, cementingPrevu,
                    waterSupplyPrevu, drillingMudPrevu, accesoriesCasingPrevu,
                    casingTubingPrevu, securityPrevu, bitsPrevu));
        }

        return result;
    }

    // Version optimisée pour tous les puits (No change needed here as it calls
    // getEtatParPhaseParPuits)
    public Map<String, List<PhaseStatus>> getEtatParPhasePourTousLesPuits() {
        Map<String, List<PhaseStatus>> result = new HashMap<>();
        List<String> puitIds = reportRepository.findDistinctPuitIds();
        for (String puitId : puitIds) {
            result.put(puitId, getEtatParPhaseParPuits(puitId));
        }
        return result;
    }

    // Méthode optimisée pour obtenir les puits avec leurs détails (No change needed
    // here)
    public List<Map<String, Object>> getAvailableWellsWithDetails() {
        return reportRepository.findDistinctPuitIds().stream()
                .map(puitId -> {
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

    // Méthode simple pour obtenir les IDs de puits (No change needed here)
    public List<String> getAvailableWells() {
        return reportRepository.findDistinctPuitIds();
    }

    // Méthode pour obtenir un résumé par puits (No change needed here)
    public Map<String, Map<String, Object>> getResumePuits() {
        Map<String, Map<String, Object>> resume = new HashMap<>();
        List<String> puitIds = reportRepository.findDistinctPuitIds();

        for (String puitId : puitIds) {
            List<Report> reports = reportRepository.findByPuitIdWithDailyCost(puitId);

            Map<String, Object> puitResume = new HashMap<>();

            if (!reports.isEmpty() && reports.get(0).getPuit() != null) {
                Puit puit = reports.get(0).getPuit();
                puitResume.put("puitName", puit.getPuitName());
                puitResume.put("location", puit.getLocation());
                puitResume.put("status", puit.getStatus());
                puitResume.put("totalDepth", puit.getTotalDepth());
            }

            puitResume.put("nombreReports", reports.size());

            double coutTotal = reports.stream()
                    .map(Report::getDailyCost)
                    .filter(Objects::nonNull)
                    .mapToDouble(dc -> dc.getDailyCost() != null ? dc.getDailyCost() : 0.0)
                    .sum();
            puitResume.put("coutTotal", coutTotal);

            OptionalDouble maxDepth = reports.stream()
                    .mapToDouble(r -> r.getDepth() != null ? r.getDepth() : 0.0)
                    .max();
            puitResume.put("profondeurActuelle", maxDepth.orElse(0.0));

            Set<String> phases = reports.stream()
                    .map(Report::getPhase)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            puitResume.put("phases", phases);

            resume.put(puitId, puitResume);
        }

        return resume;
    }

    // Helper method to sum a specific cost attribute from DailyCost
    // In MonitoringService.java

    private double sumDailyCostAttribute(List<Report> reports, java.util.function.Function<DailyCost, Double> getter) {
        return reports.stream()
                .map(Report::getDailyCost)
                .filter(Objects::nonNull)
                .map(getter) // This now correctly yields a Stream<Double>
                .filter(Objects::nonNull) // Filter out any null Doubles if they can exist
                .mapToDouble(Double::doubleValue) // Convert Stream<Double> to DoubleStream
                .sum();
    }

    private String normalize(String value) {
        return value.replaceAll("[\\s\"'']", "").toLowerCase();
    }
}