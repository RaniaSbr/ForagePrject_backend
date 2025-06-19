// src/main/java/com/prjt2cs/project/model/Notification.java
package com.prjt2cs.project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "NOTIFICATIONS")
public class Notification {

    public enum Type { INFO, WARNING, CRITICAL, ACTION }
    public enum Category { REPORT, BUDGET, DELAY, SYSTEM }
    public enum Severity { LOW, MEDIUM, HIGH, CRITICAL }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    private String title;
    private String description;
    private LocalDateTime timestamp;
    private boolean isRead;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "puit_id")
    private Puit puit;

    @Column(name = "report_id")
private Long reportId;


    // Constructeur par défaut
    public Notification() {
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    // Constructeur complet
    public Notification(Type type, Category category, Severity severity, String title, String description, Puit puit, Long reportId) {
        this.type = type;
        this.category = category;
        this.severity = severity;
        this.title = title;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
        this.puit = puit;
        this.reportId = reportId;
    }
    

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Puit getPuit() {
        return puit;
    }

    public void setPuit(Puit puit) {
        this.puit = puit;
    }
    public Long getReportId() {
        return reportId;
    }
    
    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }
    
}