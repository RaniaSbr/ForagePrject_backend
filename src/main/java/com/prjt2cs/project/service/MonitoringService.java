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
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    public MonitoringService(
        PhasePrevisionRepository previsionRepository,
        ReportRepository reportRepository,
        NotificationService notificationService,
        NotificationRepository notificationRepository
    ) {
        this.previsionRepository = previsionRepository;
        this.reportRepository = reportRepository;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }
    

    public List<PhaseStatus> getEtatParPhase() {
        List<PhaseStatus> result = new ArrayList<>();
        List<Report> allReports = reportRepository.findAll();
    
        for (PhasePrevision prevision : previsionRepository.findAll()) {
            String phaseName = prevision.getPhaseName();
    
            // Rapports associés à la phase (normalisation)
            List<Report> reportsForPhase = allReports.stream()
                    .filter(r -> r.getPhase() != null && normalize(r.getPhase()).equals(normalize(phaseName)))
                    .collect(Collectors.toList());
    
            // Somme des coûts réels
            double coutReel = reportsForPhase.stream()
                    .map(Report::getDailyCost)
                    .filter(Objects::nonNull)
                    .mapToDouble(dc -> dc.getDailyCost() != null ? dc.getDailyCost() : 0.0)
                    .sum();
    
            // Calcul du délai réel (au moins 1)
            int delaiReel = reportsForPhase.stream()
                    .map(Report::getDay)
                    .filter(Objects::nonNull)
                    .mapToInt(day -> Math.max(1, day.intValue()))
                    .max()
                    .orElse(1);
    
            // Création de l’objet PhaseStatus
            PhaseStatus phaseStatus = new PhaseStatus(
                    phaseName,
                    prevision.getTotal() != null ? prevision.getTotal() : 0.0,
                    coutReel,
                    prevision.getNombreJours() != null ? prevision.getNombreJours() : 0,
                    delaiReel
            );
    
            result.add(phaseStatus);
    
            // --- Logique de notification si DANGER ---
            String puitId = "P12345";
            String puitName = "Puit principal";
    
            // Dépassement Coût
            if ("DANGER".equals(phaseStatus.getEtatCout())) {
                String titreCout = "Dépassement critique - Coût phase " + phaseName;
                if (!notificationRepository.existsByTitleAndPuit_PuitId(titreCout, puitId)) {
                    notificationService.createNotification(
                            Notification.Type.CRITICAL,
                            Notification.Category.BUDGET,
                            titreCout,
                            "Le coût réel de la phase " + phaseName + " du puits " + puitName + " dépasse le budget prévu.",
                            puitId,
                            null
        
                    );
                }
            }
    
            // Dépassement Délai
            if ("DANGER".equals(phaseStatus.getEtatDelai())) {
                String titreDelai = "Dépassement critique - Délai phase " + phaseName;
                if (!notificationRepository.existsByTitleAndPuit_PuitId(titreDelai, puitId)) {
                    notificationService.createNotification(
                            Notification.Type.CRITICAL,
                            Notification.Category.DELAY,
                            titreDelai,
                            "Le délai réel de la phase " + phaseName + " du puits " + puitName + " dépasse le délai prévu.",
                            puitId,
                            null
               
                    );
                }
            }
        }
    
        return result;
    }
    

    private String normalize(String value) {
        return value.replaceAll("[\\s\"’']", "").toLowerCase();
    }
}
