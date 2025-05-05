package com.prjt2cs.project.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "OPERATION")
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private LocalTime startTime;
    private LocalTime endTime;

    private LocalDate date_ope;

    private String description;

    private Double initialDepth;
    private Double finalDepth;

    private String rate;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalDate getDate() {
        return date_ope;
    }

    public void setDate(LocalDate date) {
        this.date_ope = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getInitialDepth() {
        return initialDepth;
    }

    public void setInitialDepth(Double initialDepth) {
        this.initialDepth = initialDepth;
    }

    public Double getFinalDepth() {
        return finalDepth;
    }

    public void setFinalDepth(Double finalDepth) {
        this.finalDepth = finalDepth;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}