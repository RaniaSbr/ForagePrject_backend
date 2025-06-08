package com.prjt2cs.project.dto;

import java.time.LocalTime;

public class OperationDto {
    private String code;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;
    private Double initialDepth;
    private Double finalDepth;
    private String rate;

    // Constructeurs
    public OperationDto() {
    }

    public OperationDto(String code, LocalTime startTime, LocalTime endTime, String description,
            Double initialDepth, Double finalDepth, String rate) {
        this.code = code;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.initialDepth = initialDepth;
        this.finalDepth = finalDepth;
        this.rate = rate;
    }

    // Getters et Setters
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
}
