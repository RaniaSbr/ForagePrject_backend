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

    // Constructeur
    public PhaseStatus(String phaseName, double coutPrevu, double coutReel, int delaiPrevu, int delaiReel) {
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

    // --- Méthodes internes ---

    private String calculerEtat(double valeurReelle, double valeurPrevue) {
        if (valeurPrevue == 0)
            return "NORMAL"; // Évite division par zéro
        if (valeurReelle >= valeurPrevue)
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
