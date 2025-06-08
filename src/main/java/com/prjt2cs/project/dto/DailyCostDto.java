package com.prjt2cs.project.dto;

import java.time.LocalDate;

public class DailyCostDto {
    private Long id;
    private String name;
    private Double drillingRig;
    private Double mudLogging;
    private Double downwholeTools;
    private Double drillingMud;
    private Double solidControl;
    private Double electricServices;
    private Double bits;
    private Double casing;
    private Double accessoriesCasing; // Correction orthographique
    private Double casingTubing;
    private Double cementing;
    private Double rigSupervision;
    private Double communications;
    private Double waterSupply;
    private Double waterServices;
    private Double security;
    private Double dailyCost;
    private LocalDate date; // Pour référence à la date du rapport

    // Constructeurs
    public DailyCostDto() {
    }

    public DailyCostDto(Long id, String name, Double drillingRig, Double mudLogging,
            Double downwholeTools, Double drillingMud, Double solidControl,
            Double electricServices, Double bits, Double casing,
            Double accessoriesCasing, Double casingTubing, Double cementing,
            Double rigSupervision, Double communications, Double waterSupply,
            Double waterServices, Double security, Double dailyCost, LocalDate date) {
        this.id = id;
        this.name = name;
        this.drillingRig = drillingRig;
        this.mudLogging = mudLogging;
        this.downwholeTools = downwholeTools;
        this.drillingMud = drillingMud;
        this.solidControl = solidControl;
        this.electricServices = electricServices;
        this.bits = bits;
        this.casing = casing;
        this.accessoriesCasing = accessoriesCasing;
        this.casingTubing = casingTubing;
        this.cementing = cementing;
        this.rigSupervision = rigSupervision;
        this.communications = communications;
        this.waterSupply = waterSupply;
        this.waterServices = waterServices;
        this.security = security;
        this.dailyCost = dailyCost;
        this.date = date;
    }

    // Getters et Setters
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

    public Double getAccessoriesCasing() {
        return accessoriesCasing;
    }

    public void setAccessoriesCasing(Double accessoriesCasing) {
        this.accessoriesCasing = accessoriesCasing;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // // Méthode utilitaire pour convertir depuis l'entité
    // public static DailyCostDTO fromEntity(DailyCost dailyCost) {
    // if (dailyCost == null) {
    // return null;
    // }

    // return new DailyCostDTO(
    // dailyCost.getId(),
    // dailyCost.getName(),
    // dailyCost.getDrillingRig(),
    // dailyCost.getMudLogging(),
    // dailyCost.getDownwholeTools(),
    // dailyCost.getDrillingMud(),
    // dailyCost.getSolidControl(),
    // dailyCost.getElectricServices(),
    // dailyCost.getBits(),
    // dailyCost.getCasing(),
    // dailyCost.getAccesoriesCasing(),
    // dailyCost.getCasingTubing(),
    // dailyCost.getCementing(),
    // dailyCost.getRigSupervision(),
    // dailyCost.getCommunications(),
    // dailyCost.getWaterSupply(),
    // dailyCost.getWaterServices(),
    // dailyCost.getSecurity(),
    // dailyCost.getDailyCost(),
    // dailyCost.getReport() != null ? dailyCost.getReport().getDate() : null);
    // }

    // // Méthode utilitaire pour convertir vers l'entité
    // public DailyCost toEntity() {
    // DailyCost dailyCost = new DailyCost();
    // dailyCost.setId(this.id);
    // dailyCost.setName(this.name);
    // dailyCost.setDrillingRig(this.drillingRig);
    // dailyCost.setMudLogging(this.mudLogging);
    // dailyCost.setDownwholeTools(this.downwholeTools);
    // dailyCost.setDrillingMud(this.drillingMud);
    // dailyCost.setSolidControl(this.solidControl);
    // dailyCost.setElectricServices(this.electricServices);
    // dailyCost.setBits(this.bits);
    // dailyCost.setCasing(this.casing);
    // dailyCost.setAccesoriesCasing(this.accessoriesCasing);
    // dailyCost.setCasingTubing(this.casingTubing);
    // dailyCost.setCementing(this.cementing);
    // dailyCost.setRigSupervision(this.rigSupervision);
    // dailyCost.setCommunications(this.communications);
    // dailyCost.setWaterSupply(this.waterSupply);
    // dailyCost.setWaterServices(this.waterServices);
    // dailyCost.setSecurity(this.security);
    // dailyCost.setDailyCost(this.dailyCost);

    // return dailyCost;
    // }
}