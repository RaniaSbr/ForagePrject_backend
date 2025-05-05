// DailyCost.java
package com.prjt2cs.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "DAILY_COST")
public class DailyCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double drillingRig; // Changed to camelCase
    private Double mudLogging; // Changed to camelCase
    private Double downwholeTools; // Changed to camelCase
    private Double drillingMud; // Changed to camelCase
    private Double solidControl; // Changed to camelCase
    private Double electricServices; // Changed to camelCase
    private Double bits; // Changed to camelCase
    private Double casing; // Changed to camelCase
    private Double accesoriesCasing; // Changed to camelCase
    private Double casingTubing; // Changed to camelCase
    private Double cementing; // Changed to camelCase
    private Double rigSupervision; // Changed to camelCase
    private Double communications; // Changed to camelCase
    private Double waterSupply; // Changed to camelCase
    private Double waterServices; // Changed to camelCase
    private Double security; // Changed to camelCase
    private Double dailyCost; // Changed to camelCase

    @OneToOne
    @JoinColumn(name = "report_id")
    private Report report;

    // Constructors
    public DailyCost() {
    }

    public DailyCost(String name, Double drillingRig, Double mudLogging, Double downwholeTools,
            Double drillingMud, Double solidControl, Double electricServices, Double bits,
            Double casing, Double accesoriesCasing, Double casingTubing, Double cementing,
            Double rigSupervision, Double communications, Double waterSupply,
            Double waterServices, Double security, Double dailyCost) {
        this.name = name;
        this.drillingRig = drillingRig;
        this.mudLogging = mudLogging;
        this.downwholeTools = downwholeTools;
        this.drillingMud = drillingMud;
        this.solidControl = solidControl;
        this.electricServices = electricServices;
        this.bits = bits;
        this.casing = casing;
        this.accesoriesCasing = accesoriesCasing;
        this.casingTubing = casingTubing;
        this.cementing = cementing;
        this.rigSupervision = rigSupervision;
        this.communications = communications;
        this.waterSupply = waterSupply;
        this.waterServices = waterServices;
        this.security = security;
        this.dailyCost = dailyCost;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDrillingRig() {
        return drillingRig;
    }

    public void setDrillingRig(Double drillingRig) {
        this.drillingRig = drillingRig;
    }

    public Double getMudLogging() {
        return mudLogging;
    }

    public void setMudLogging(Double mudLogging) {
        this.mudLogging = mudLogging;
    }

    public Double getDownwholeTools() {
        return downwholeTools;
    }

    public void setDownwholeTools(Double downwholeTools) {
        this.downwholeTools = downwholeTools;
    }

    public Double getDrillingMud() {
        return drillingMud;
    }

    public void setDrillingMud(Double drillingMud) {
        this.drillingMud = drillingMud;
    }

    public Double getSolidControl() {
        return solidControl;
    }

    public void setSolidControl(Double solidControl) {
        this.solidControl = solidControl;
    }

    public Double getElectricServices() {
        return electricServices;
    }

    public void setElectricServices(Double electricServices) {
        this.electricServices = electricServices;
    }

    public Double getBits() {
        return bits;
    }

    public void setBits(Double bits) {
        this.bits = bits;
    }

    public Double getCasing() {
        return casing;
    }

    public void setCasing(Double casing) {
        this.casing = casing;
    }

    public Double getAccesoriesCasing() {
        return accesoriesCasing;
    }

    public void setAccesoriesCasing(Double accesoriesCasing) {
        this.accesoriesCasing = accesoriesCasing;
    }

    public Double getCasingTubing() {
        return casingTubing;
    }

    public void setCasingTubing(Double casingTubing) {
        this.casingTubing = casingTubing;
    }

    public Double getCementing() {
        return cementing;
    }

    public void setCementing(Double cementing) {
        this.cementing = cementing;
    }

    public Double getRigSupervision() {
        return rigSupervision;
    }

    public void setRigSupervision(Double rigSupervision) {
        this.rigSupervision = rigSupervision;
    }

    public Double getCommunications() {
        return communications;
    }

    public void setCommunications(Double communications) {
        this.communications = communications;
    }

    public Double getWaterSupply() {
        return waterSupply;
    }

    public void setWaterSupply(Double waterSupply) {
        this.waterSupply = waterSupply;
    }

    public Double getWaterServices() {
        return waterServices;
    }

    public void setWaterServices(Double waterServices) {
        this.waterServices = waterServices;
    }

    public Double getSecurity() {
        return security;
    }

    public void setSecurity(Double security) {
        this.security = security;
    }

    public Double getDailyCost() {
        return dailyCost;
    }

    public void setDailyCost(Double dailyCost) {
        this.dailyCost = dailyCost;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
