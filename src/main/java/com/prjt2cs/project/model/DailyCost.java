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
    private Double DrillingRig;
    private Double MudLogging;
    private Double DownwholeTools;
    private Double DrillingMud;
    private Double SolidControl;
    private Double ElectricServices;
    private Double Bits;
    private Double Casing;
    private Double AccesoriesCasing;
    private Double CasingTubing;
    private Double Cementing;
    private Double RigSupervision;
    private Double Communications;
    private Double WaterSupply;
    private Double WaterServices;
    private Double Security;
    private Double DailyCost;

    @OneToOne
    @JoinColumn(name = "report_id")
    private Report report;

    // Constructeurs
    public DailyCost() {
    }

    public DailyCost(String name, Double DrillingRig, Double MudLogging, Double DownwholeTools,
            Double DrillingMud, Double SolidControl, Double ElectricServices, Double Bits,
            Double Casing, Double AccesoriesCasing, Double CasingTubing, Double Cementing,
            Double RigSupervision, Double Communications, Double WaterSupply,
            Double WaterServices, Double Security, Double DailyCost) {
        this.name = name;
        this.DrillingRig = DrillingRig;
        this.MudLogging = MudLogging;
        this.DownwholeTools = DownwholeTools;
        this.DrillingMud = DrillingMud;
        this.SolidControl = SolidControl;
        this.ElectricServices = ElectricServices;
        this.Bits = Bits;
        this.Casing = Casing;
        this.AccesoriesCasing = AccesoriesCasing;
        this.CasingTubing = CasingTubing;
        this.Cementing = Cementing;
        this.RigSupervision = RigSupervision;
        this.Communications = Communications;
        this.WaterSupply = WaterSupply;
        this.WaterServices = WaterServices;
        this.Security = Security;
        this.DailyCost = DailyCost;
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
        return DrillingRig;
    }

    public void setDrillingRig(Double drillingRig) {
        this.DrillingRig = drillingRig;
    }

    public Double getMudLogging() {
        return MudLogging;
    }

    public void setMudLogging(Double mudLogging) {
        this.MudLogging = mudLogging;
    }

    public Double getDownwholeTools() {
        return DownwholeTools;
    }

    public void setDownwholeTools(Double downwholeTools) {
        this.DownwholeTools = downwholeTools;
    }

    public Double getDrillingMud() {
        return DrillingMud;
    }

    public void setDrillingMud(Double drillingMud) {
        this.DrillingMud = drillingMud;
    }

    public Double getSolidControl() {
        return SolidControl;
    }

    public void setSolidControl(Double solidControl) {
        this.SolidControl = solidControl;
    }

    public Double getElectricServices() {
        return ElectricServices;
    }

    public void setElectricServices(Double electricServices) {
        this.ElectricServices = electricServices;
    }

    public Double getBits() {
        return Bits;
    }

    public void setBits(Double bits) {
        this.Bits = bits;
    }

    public Double getCasing() {
        return Casing;
    }

    public void setCasing(Double casing) {
        this.Casing = casing;
    }

    public Double getAccesoriesCasing() {
        return AccesoriesCasing;
    }

    public void setAccesoriesCasing(Double accesoriesCasing) {
        this.AccesoriesCasing = accesoriesCasing;
    }

    public Double getCasingTubing() {
        return CasingTubing;
    }

    public void setCasingTubing(Double casingTubing) {
        this.CasingTubing = casingTubing;
    }

    public Double getCementing() {
        return Cementing;
    }

    public void setCementing(Double cementing) {
        this.Cementing = cementing;
    }

    public Double getRigSupervision() {
        return RigSupervision;
    }

    public void setRigSupervision(Double rigSupervision) {
        this.RigSupervision = rigSupervision;
    }

    public Double getCommunications() {
        return Communications;
    }

    public void setCommunications(Double communications) {
        this.Communications = communications;
    }

    public Double getWaterSupply() {
        return WaterSupply;
    }

    public void setWaterSupply(Double waterSupply) {
        this.WaterSupply = waterSupply;
    }

    public Double getWaterServices() {
        return WaterServices;
    }

    public void setWaterServices(Double waterServices) {
        this.WaterServices = waterServices;
    }

    public Double getSecurity() {
        return Security;
    }

    public void setSecurity(Double security) {
        this.Security = security;
    }

    public Double getDailyCost() {
        return DailyCost;
    }

    public void setDailyCost(Double dailyCost) {
        this.DailyCost = dailyCost;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    @Override
    public String toString() {
        return "DailyCost{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", DrillingRig=" + DrillingRig +
                ", MudLogging=" + MudLogging +
                ", DownwholeTools=" + DownwholeTools +
                ", DrillingMud=" + DrillingMud +
                ", SolidControl=" + SolidControl +
                ", ElectricServices=" + ElectricServices +
                ", Bits=" + Bits +
                ", Casing=" + Casing +
                ", AccesoriesCasing=" + AccesoriesCasing +
                ", CasingTubing=" + CasingTubing +
                ", Cementing=" + Cementing +
                ", RigSupervision=" + RigSupervision +
                ", Communications=" + Communications +
                ", WaterSupply=" + WaterSupply +
                ", WaterServices=" + WaterServices +
                ", Security=" + Security +
                ", DailyCost=" + DailyCost +
                '}';
    }
}