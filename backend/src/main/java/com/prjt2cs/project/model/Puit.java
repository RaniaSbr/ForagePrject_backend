package com.prjt2cs.project.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "PUIT")
public class Puit {

    @Id
    @Column(name = "PUIT_ID")
    private String puitId; // ex : "Puit 1", "Puit 2"

    @Column(name = "PUIT_NAME")
    private String puitName;

    @Column(name = "LOCATION")
    private String location;

    @Column(name = "TOTAL_DEPTH")
    private Double totalDepth;

    @Column(name = "STATUS")
    private String status; // ex : "En cours", "Terminé", "Abandonné"

    // Relation bidirectionnelle avec Report
    @OneToMany(mappedBy = "puit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Report> reports = new ArrayList<>();

    // Constructeurs
    public Puit() {
        this.reports = new ArrayList<>();
    }

    public Puit(String puitId) {
        this.puitId = puitId;
        this.reports = new ArrayList<>();
    }

    // Getters et Setters existants
    public String getPuitId() {
        return puitId;
    }

    public void setPuitId(String puitId) {
        this.puitId = puitId;
    }

    public String getPuitName() {
        return puitName;
    }

    public void setPuitName(String puitName) {
        this.puitName = puitName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getTotalDepth() {
        return totalDepth;
    }

    public void setTotalDepth(Double totalDepth) {
        this.totalDepth = totalDepth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getters et setters pour les reports
    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports != null ? reports : new ArrayList<>();
        // Maintenir la cohérence bidirectionnelle
        if (this.reports != null) {
            for (Report report : this.reports) {
                report.setPuit(this);
            }
        }
    }

    // Méthodes utilitaires pour gérer les reports
    public void addReport(Report report) {
        if (this.reports == null) {
            this.reports = new ArrayList<>();
        }
        this.reports.add(report);
        report.setPuit(this);
    }

    public void removeReport(Report report) {
        if (this.reports != null) {
            this.reports.remove(report);
            report.setPuit(null);
        }
    }

    public int getNombreReports() {
        return this.reports != null ? this.reports.size() : 0;
    }

    // Méthodes utilitaires supplémentaires
    public List<Report> getReportsByPhase(String phase) {
        return reports.stream()
                .filter(report -> phase.equals(report.getPhase()))
                .toList();
    }

    public Report getLatestReport() {
        return reports.stream()
                .max((r1, r2) -> r1.getDate().compareTo(r2.getDate()))
                .orElse(null);
    }
}