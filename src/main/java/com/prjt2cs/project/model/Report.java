package com.prjt2cs.project.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "REP")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PHASE")
    private String phase;

    @Column(name = "DEPTH")
    private Double depth;

    @Column(name = "PLANNED_OPE")
    private String plannedope;

    @Column(name = "REPORT_DATE")
    private LocalDate date;

    @Column(name = "TVD")
    private Double tvd;

    @Column(name = "DAILY_PROGRESS")
    private Double drillingProgress;

    @Column(name = "DRILLING_HOURS")
    private Double drillingHours;

    @Column(name = "ACTUAL_DAY")
    private Double actualDay;

    @Column(name = "REMARKS", length = 4000)
    private String remarks;

    // Champs pour le fichier Excel
    @Lob
    @Column(name = "EXCEL_FILE")
    private byte[] excelFile;

    // NOUVELLE RELATION AVEC PUIT
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PUIT_ID", nullable = false)
    @JsonBackReference
    private Puit puit;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Operation> operations;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "daily_cost_id")
    @JsonManagedReference
    private DailyCost dailyCost;

    // Constructeurs
    public Report() {
    }

    public Report(Puit puit) {
        this.puit = puit;
    }

    // Getters et Setters existants
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRemarks(List<String> remarksList) {
        this.remarks = remarksList != null ? String.join("|||", remarksList) : null;
    }

    public List<String> getRemarks() {
        return remarks != null ? Arrays.asList(remarks.split("\\|\\|\\|")) : new ArrayList<>();
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getPhase() {
        return phase;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public Double getDay() {
        return actualDay;
    }

    public void setDay(Double actualDay) {
        this.actualDay = actualDay;
    }

    public String getPlannedOperation() {
        return plannedope;
    }

    public void setPlannedOperation(String plannedope) {
        this.plannedope = plannedope;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public void addOperation(Operation op) {
        operations.add(op);
        op.setReport(this);
    }

    public DailyCost getDailyCost() {
        return dailyCost;
    }

    public void setDailyCost(DailyCost dailyCost) {
        this.dailyCost = dailyCost;
        if (dailyCost != null) {
            dailyCost.setReport(this);
        }
    }

    public Double getTvd() {
        return tvd;
    }

    public void setTvd(Double tvd) {
        this.tvd = tvd;
    }

    public Double getDrillingProgress() {
        return drillingProgress;
    }

    public void setDrillingProgress(Double drillingProgress) {
        this.drillingProgress = drillingProgress;
    }

    public Double getDrillingHours() {
        return drillingHours;
    }

    public void setDrillingHours(Double drillingHours) {
        this.drillingHours = drillingHours;
    }

    // Getters et setters pour le fichier Excel
    public byte[] getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(byte[] excelFile) {
        this.excelFile = excelFile;
    }

    // NOUVEAUX GETTERS ET SETTERS POUR PUIT
    public Puit getPuit() {
        return puit;
    }

    public void setPuit(Puit puit) {
        this.puit = puit;
    }

    // Méthodes utilitaires
    public boolean hasExcelFile() {
        return excelFile != null && excelFile.length > 0;
    }

    public void clearExcelFile() {
        this.excelFile = null;
    }

    @Column(name = "ANOMALIES", length = 2000)
    private String anomalies;

    @Column(name = "EXPERT_ANALYSIS", length = 2000)
    private String expertAnalysis;

    @Column(name = "EXPERT_RECOMMENDATIONS", length = 2000)
    private String expertRecommendations;

    @Column(name = "STATUS")
private String status = "En attente";

    public String getAnomalies() {
        return anomalies;
    }

    public void setAnomalies(String anomalie) {
        this.anomalies = anomalie;
    }

    public String getAnalysis() {
        return expertAnalysis;
    }

    public void setAnalysis(String expertAnalysis) {
        this.expertAnalysis = expertAnalysis;
    }

    public String getRecommendations() {
        return expertRecommendations;
    }

    public void setRecommendations(String expertRecommendations) {
        this.expertRecommendations = expertRecommendations;
    }

    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    

    // Méthode utilitaire pour obtenir l'ID du puit
    public String getPuitId() {
        return puit != null ? puit.getPuitId() : null;
    }

    // Méthode utilitaire pour obtenir le nom du puit
    public String getPuitName() {
        return puit != null ? puit.getPuitName() : null;
    }
}