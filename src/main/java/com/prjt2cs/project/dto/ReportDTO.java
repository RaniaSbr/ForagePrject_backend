package com.prjt2cs.project.dto;

import java.time.LocalDate;
import java.util.List;

import com.prjt2cs.project.model.DailyCost;
import com.prjt2cs.project.model.Operation;

public class ReportDTO {

    private Long id;
    private List<String> remarks;
    private String phase;
    private Double depth;
    private String plannedOperation;
    private LocalDate date;
    private String anomalies;
    private String analysis;
    private String recommendations;
    private String status;
    private Double tvd;
    private Double drillingProgress;
    private Double drillingHours;
    private Double day;
    private List<Operation> operations;
    private DailyCost dailyCost;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getRemarks() {
        return remarks;
    }

    public void setRemarks(List<String> remarks) {
        this.remarks = remarks;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public String getPlannedOperation() {
        return plannedOperation;
    }

    public void setPlannedOperation(String plannedOperation) {
        this.plannedOperation = plannedOperation;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getAnomalies() {
        return anomalies;
    }

    public void setAnomalies(String anomalies) {
        this.anomalies = anomalies;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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

    public Double getDay() {
        return day;
    }

    public void setDay(Double day) {
        this.day = day;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public DailyCost getDailyCost() {
        return dailyCost;
    }

    public void setDailyCost(DailyCost dailyCost) {
        this.dailyCost = dailyCost;
    }
}
