package com.prjt2cs.project.dto;

public class PhaseStatus {
    private String phaseName;
    private double coutPrevu;
    private double coutReel;
    private int delaiPrevu;
    private int delaiReel;
    private boolean depassementCout;
    private boolean depassementDelai;

    private String etatCout; // NORMAL / RISQUE / DANGER
    private String etatDelai; // NORMAL / RISQUE / DANGER
    private String couleurCout; // V / O / R
    private String couleurDelai; // V / O / R

    // New detailed cost attributes (Actual values)
    // private Double drillingActual;
    private Double mudLoggingActual;
    private Double cementingActual;
    private Double waterSupplyActual;
    private Double drillingMudActual;
    private Double accesoriesCasingActual;
    private Double casingTubingActual;
    private Double securityActual;
    private Double bitsActual;

    // New detailed cost prevision attributes (Prevision values, assuming they are
    // also needed for comparison)
    // If these come from a different source than PhasePrevision.total, you'll need
    // to adjust.
    // For now, I'm assuming the *total* prevision from PhasePrevision is
    // distributed amongst these,
    // or that these previsions are also available per-phase in PhasePrevision or
    // another model.
    // **IMPORTANT**: You need to define where these 'prevision' values for each
    // category come from.
    // For this example, I'll assume they are passed in or derived.
    // If your 'PhasePrevision' model doesn't store these individual previsions,
    // you'll need to modify PhasePrevision or your data source to provide them.
    private Double drillingPrevu;
    private Double mudLoggingPrevu;
    private Double cementingPrevu;
    private Double waterSupplyPrevu;
    private Double drillingMudPrevu;
    private Double accesoriesCasingPrevu;
    private Double casingTubingPrevu;
    private Double securityPrevu;
    private Double bitsPrevu;

    // Depassement, Etat, and Couleur for detailed costs
    private boolean depassementDrillingCout;
    private String etatDrillingCout;
    private String couleurDrillingCout;

    private boolean depassementMudLoggingCout;
    private String etatMudLoggingCout;
    private String couleurMudLoggingCout;

    private boolean depassementCementingCout;
    private String etatCementingCout;
    private String couleurCementingCout;

    private boolean depassementWaterSupplyCout;
    private String etatWaterSupplyCout;
    private String couleurWaterSupplyCout;

    private boolean depassementDrillingMudCout;
    private String etatDrillingMudCout;
    private String couleurDrillingMudCout;

    private boolean depassementAccesoriesCasingCout;
    private String etatAccesoriesCasingCout;
    private String couleurAccesoriesCasingCout;

    private boolean depassementCasingTubingCout;
    private String etatCasingTubingCout;
    private String couleurCasingTubingCout;

    private boolean depassementSecurityCout;
    private String etatSecurityCout;
    private String couleurSecurityCout;

    private boolean depassementBitsCout;
    private String etatBitsCout;
    private String couleurBitsCout;

    // Constructor - Updated to include prevision for detailed costs
    public PhaseStatus(String phaseName, double coutPrevu, double coutReel, int delaiPrevu, int delaiReel,
            Double drillingActual, Double mudLoggingActual, Double cementingActual,
            Double waterSupplyActual, Double drillingMudActual, Double accesoriesCasingActual,
            Double casingTubingActual, Double securityActual, Double bitsActual,
            // Prevision values for detailed costs
            Double drillingPrevu, Double mudLoggingPrevu, Double cementingPrevu,
            Double waterSupplyPrevu, Double drillingMudPrevu, Double accesoriesCasingPrevu,
            Double casingTubingPrevu, Double securityPrevu, Double bitsPrevu) {

        this.phaseName = phaseName;
        this.coutPrevu = coutPrevu;
        this.coutReel = coutReel;
        this.delaiPrevu = delaiPrevu;
        this.delaiReel = delaiReel;
        this.depassementCout = coutReel > coutPrevu;
        this.depassementDelai = delaiReel > delaiPrevu;

        this.etatCout = calculerEtat(coutReel, coutPrevu);
        this.etatDelai = calculerEtat(delaiReel, delaiPrevu);
        this.couleurCout = mapCouleur(this.etatCout);
        this.couleurDelai = mapCouleur(this.etatDelai);

        // this.drillingActual = drillingActual;
        this.mudLoggingActual = mudLoggingActual;
        this.cementingActual = cementingActual;
        this.waterSupplyActual = waterSupplyActual;
        this.drillingMudActual = drillingMudActual;
        this.accesoriesCasingActual = accesoriesCasingActual;
        this.casingTubingActual = casingTubingActual;
        this.securityActual = securityActual;
        this.bitsActual = bitsActual;

        // Set prevision values for detailed costs
        this.drillingPrevu = (drillingPrevu != null) ? drillingPrevu : 0.0;
        this.mudLoggingPrevu = (mudLoggingPrevu != null) ? mudLoggingPrevu : 0.0;
        this.cementingPrevu = (cementingPrevu != null) ? cementingPrevu : 0.0;
        this.waterSupplyPrevu = (waterSupplyPrevu != null) ? waterSupplyPrevu : 0.0;
        this.drillingMudPrevu = (drillingMudPrevu != null) ? drillingMudPrevu : 0.0;
        this.accesoriesCasingPrevu = (accesoriesCasingPrevu != null) ? accesoriesCasingPrevu : 0.0;
        this.casingTubingPrevu = (casingTubingPrevu != null) ? casingTubingPrevu : 0.0;
        this.securityPrevu = (securityPrevu != null) ? securityPrevu : 0.0;
        this.bitsPrevu = (bitsPrevu != null) ? bitsPrevu : 0.0;

        // Calculate status for each detailed cost
        this.depassementDrillingCout = drillingActual > this.drillingPrevu;
        this.etatDrillingCout = calculerEtat(drillingActual, this.drillingPrevu);
        this.couleurDrillingCout = mapCouleur(this.etatDrillingCout);

        this.depassementMudLoggingCout = mudLoggingActual > this.mudLoggingPrevu;
        this.etatMudLoggingCout = calculerEtat(mudLoggingActual, this.mudLoggingPrevu);
        this.couleurMudLoggingCout = mapCouleur(this.etatMudLoggingCout);

        this.depassementCementingCout = cementingActual > this.cementingPrevu;
        this.etatCementingCout = calculerEtat(cementingActual, this.cementingPrevu);
        this.couleurCementingCout = mapCouleur(this.etatCementingCout);

        this.depassementWaterSupplyCout = waterSupplyActual > this.waterSupplyPrevu;
        this.etatWaterSupplyCout = calculerEtat(waterSupplyActual, this.waterSupplyPrevu);
        this.couleurWaterSupplyCout = mapCouleur(this.etatWaterSupplyCout);

        this.depassementDrillingMudCout = drillingMudActual > this.drillingMudPrevu;
        this.etatDrillingMudCout = calculerEtat(drillingMudActual, this.drillingMudPrevu);
        this.couleurDrillingMudCout = mapCouleur(this.etatDrillingMudCout);

        this.depassementAccesoriesCasingCout = accesoriesCasingActual > this.accesoriesCasingPrevu;
        this.etatAccesoriesCasingCout = calculerEtat(accesoriesCasingActual, this.accesoriesCasingPrevu);
        this.couleurAccesoriesCasingCout = mapCouleur(this.etatAccesoriesCasingCout);

        this.depassementCasingTubingCout = casingTubingActual > this.casingTubingPrevu;
        this.etatCasingTubingCout = calculerEtat(casingTubingActual, this.casingTubingPrevu);
        this.couleurCasingTubingCout = mapCouleur(this.etatCasingTubingCout);

        this.depassementSecurityCout = securityActual > this.securityPrevu;
        this.etatSecurityCout = calculerEtat(securityActual, this.securityPrevu);
        this.couleurSecurityCout = mapCouleur(this.etatSecurityCout);

        this.depassementBitsCout = bitsActual > this.bitsPrevu;
        this.etatBitsCout = calculerEtat(bitsActual, this.bitsPrevu);
        this.couleurBitsCout = mapCouleur(this.etatBitsCout);
    }

    // --- Getters ---
    public String getPhaseName() {
        return phaseName;
    }

    public double getCoutPrevu() {
        return coutPrevu;
    }

    public double getCoutReel() {
        return coutReel;
    }

    public int getDelaiPrevu() {
        return delaiPrevu;
    }

    public int getDelaiReel() {
        return delaiReel;
    }

    public boolean isDepassementCout() {
        return depassementCout;
    }

    public boolean isDepassementDelai() {
        return depassementDelai;
    }

    public String getEtatCout() {
        return etatCout;
    }

    public String getEtatDelai() {
        return etatDelai;
    }

    public String getCouleurCout() {
        return couleurCout;
    }

    public String getCouleurDelai() {
        return couleurDelai;
    }

    // Getters for Actual Detailed Costs
    // public Double getDrillingActual() {
    // return drillingActual;
    // }

    public Double getMudLoggingActual() {
        return mudLoggingActual;
    }

    public Double getCementingActual() {
        return cementingActual;
    }

    public Double getWaterSupplyActual() {
        return waterSupplyActual;
    }

    public Double getDrillingMudActual() {
        return drillingMudActual;
    }

    public Double getAccesoriesCasingActual() {
        return accesoriesCasingActual;
    }

    public Double getCasingTubingActual() {
        return casingTubingActual;
    }

    public Double getSecurityActual() {
        return securityActual;
    }

    public Double getBitsActual() {
        return bitsActual;
    }

    // Getters for Prevision Detailed Costs
    public Double getDrillingPrevu() {
        return drillingPrevu;
    }

    public Double getMudLoggingPrevu() {
        return mudLoggingPrevu;
    }

    public Double getCementingPrevu() {
        return cementingPrevu;
    }

    public Double getWaterSupplyPrevu() {
        return waterSupplyPrevu;
    }

    public Double getDrillingMudPrevu() {
        return drillingMudPrevu;
    }

    public Double getAccesoriesCasingPrevu() {
        return accesoriesCasingPrevu;
    }

    public Double getCasingTubingPrevu() {
        return casingTubingPrevu;
    }

    public Double getSecurityPrevu() {
        return securityPrevu;
    }

    public Double getBitsPrevu() {
        return bitsPrevu;
    }

    // Getters for Depassement, Etat, Couleur for Detailed Costs
    public boolean isDepassementDrillingCout() {
        return depassementDrillingCout;
    }

    public String getEtatDrillingCout() {
        return etatDrillingCout;
    }

    public String getCouleurDrillingCout() {
        return couleurDrillingCout;
    }

    public boolean isDepassementMudLoggingCout() {
        return depassementMudLoggingCout;
    }

    public String getEtatMudLoggingCout() {
        return etatMudLoggingCout;
    }

    public String getCouleurMudLoggingCout() {
        return couleurMudLoggingCout;
    }

    public boolean isDepassementCementingCout() {
        return depassementCementingCout;
    }

    public String getEtatCementingCout() {
        return etatCementingCout;
    }

    public String getCouleurCementingCout() {
        return couleurCementingCout;
    }

    public boolean isDepassementWaterSupplyCout() {
        return depassementWaterSupplyCout;
    }

    public String getEtatWaterSupplyCout() {
        return etatWaterSupplyCout;
    }

    public String getCouleurWaterSupplyCout() {
        return couleurWaterSupplyCout;
    }

    public boolean isDepassementDrillingMudCout() {
        return depassementDrillingMudCout;
    }

    public String getEtatDrillingMudCout() {
        return etatDrillingMudCout;
    }

    public String getCouleurDrillingMudCout() {
        return couleurDrillingMudCout;
    }

    public boolean isDepassementAccesoriesCasingCout() {
        return depassementAccesoriesCasingCout;
    }

    public String getEtatAccesoriesCasingCout() {
        return etatAccesoriesCasingCout;
    }

    public String getCouleurAccesoriesCasingCout() {
        return couleurAccesoriesCasingCout;
    }

    public boolean isDepassementCasingTubingCout() {
        return depassementCasingTubingCout;
    }

    public String getEtatCasingTubingCout() {
        return etatCasingTubingCout;
    }

    public String getCouleurCasingTubingCout() {
        return couleurCasingTubingCout;
    }

    public boolean isDepassementSecurityCout() {
        return depassementSecurityCout;
    }

    public String getEtatSecurityCout() {
        return etatSecurityCout;
    }

    public String getCouleurSecurityCout() {
        return couleurSecurityCout;
    }

    public boolean isDepassementBitsCout() {
        return depassementBitsCout;
    }

    public String getEtatBitsCout() {
        return etatBitsCout;
    }

    public String getCouleurBitsCout() {
        return couleurBitsCout;
    }

    // --- Internal Methods ---

    private String calculerEtat(double valeurReelle, double valeurPrevue) {
        if (valeurPrevue == 0) {
            // If prevision is zero, consider it normal unless actual is positive (then it's
            // a danger if it's not 0)
            return (valeurReelle > 0) ? "DANGER" : "NORMAL";
        }
        if (valeurReelle > valeurPrevue)
            return "DANGER";
        if (valeurReelle >= 0.8 * valeurPrevue)
            return "RISQUE";
        return "NORMAL";
    }

    private String mapCouleur(String etat) {
        return switch (etat) {
            case "DANGER" -> "R";
            case "RISQUE" -> "O";
            default -> "V";
        };
    }
}