package com.prjt2cs.project.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "REP")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ACTIVITY")
    private String activity;

    @Column(name = "DEPTH")
    private Double depth;

    @Column(name = "COST")
    private Double cost;

    @Column(name = "REPORT_DATE")
    private LocalDate date;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Operation> operations = new ArrayList<>();

    @OneToOne
    @JsonManagedReference
    private DailyCost dailyCost;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
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
}