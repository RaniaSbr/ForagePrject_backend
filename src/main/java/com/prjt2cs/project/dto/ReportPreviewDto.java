package com.prjt2cs.project.dto;

import com.prjt2cs.project.model.DailyCost;
import com.prjt2cs.project.model.Operation;
import java.time.LocalDate;
import java.util.List;

public class ReportPreviewDto {
    private Long id;
    private String phase;
    private Double depth;
    private String plannedOperation;
    private LocalDate date;
    private Double tvd;
    private Double drillingProgress;
    private Double drillingHours;
    private Double day;
    private List<String> remarks;
    private List<Operation> operations;
    private DailyCost dailyCost;
    private byte[] excelFile;

    // Constructors
    public ReportPreviewDto() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<String> getRemarks() {
        return remarks;
    }

    public void setRemarks(List<String> remarks) {
        this.remarks = remarks;
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

    public byte[] getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(byte[] excelFile) {
        this.excelFile = excelFile;
    }
}