package com.prjt2cs.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "PREVISION_PHASE")

public class PhasePrevision {

    @Id
    @Column(length = 10)
    private String phaseId; // ex : "26''", "16''", "12’’ 1/4", "8’’ 1/2"
    private Double drilling;
    private Double mudLogging;
    private Double cementing;
    private Double waterSupply;
    private Double drillingMud;
    private Double accesoriesCasing;
    private Double casingTubing;
    private Double security;
    private Double bits;
    private Double total;
    private Double depth;

    private String phaseName;
    private Double wellHead;
    private Double runCasing;
    private Double corring;
    private Double logging;
    private Double testing;

    private Integer nombreJours; // délai en jours

    public PhasePrevision() {
    }

    public PhasePrevision(String phaseId) {
        this.phaseId = phaseId;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public Double setDepth(Double depth) {
        this.depth = depth;
        return this.depth;
    }

    public Double getDepth() {
        return depth;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public String getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(String phaseId) {
        this.phaseId = phaseId;
    }

    public Double getDrilling() {
        return drilling;
    }

    public void setDrilling(Double drilling) {
        this.drilling = drilling;
    }

    public Double getMudLogging() {
        return mudLogging;
    }

    public void setMudLogging(Double mudLogging) {
        this.mudLogging = mudLogging;
    }

    public Double getCementing() {
        return cementing;
    }

    public void setCementing(Double cementing) {
        this.cementing = cementing;
    }

    public Double getWaterSupply() {
        return waterSupply;
    }

    public void setWaterSupply(Double waterSupply) {
        this.waterSupply = waterSupply;
    }

    public Double getWellHead() {
        return wellHead;
    }

    public void setWellHead(Double wellHead) {
        this.wellHead = wellHead;
    }

    public Double getCasingTubing() {
        return casingTubing;
    }

    public void setCasingTubing(Double casingTubing) {
        this.casingTubing = casingTubing;
    }

    public Double getAccesoriesCasing() {
        return accesoriesCasing;
    }

    public void setAccesoriesCasing(Double casingAccessoire) {
        this.accesoriesCasing = casingAccessoire;
    }

    public Double getRunCasing() {
        return runCasing;
    }

    public void setRunCasing(Double runCasing) {
        this.runCasing = runCasing;
    }

    public Double getDrillingBit() {
        return bits;
    }

    public void setDrillingBit(Double drillingBit) {
        this.bits = drillingBit;
    }

    public Double getCorring() {
        return corring;
    }

    public void setCorring(Double corring) {
        this.corring = corring;
    }

    public Double getDrillingMud() {
        return drillingMud;
    }

    public void setDrillingMud(Double drillingMud) {
        this.drillingMud = drillingMud;
    }

    public Double getLogging() {
        return logging;
    }

    public void setLogging(Double logging) {
        this.logging = logging;
    }

    public Double getTesting() {
        return testing;
    }

    public void setTesting(Double testing) {
        this.testing = testing;
    }

    public Double getSecurite() {
        return security;
    }

    public void setSecurite(Double securite) {
        this.security = securite;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Integer getNombreJours() {
        return nombreJours;
    }

    public void setNombreJours(Integer nombreJours) {
        this.nombreJours = nombreJours;
    }
}
