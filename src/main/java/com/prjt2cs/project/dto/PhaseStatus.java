package com.prjt2cs.project.dto;

public class PhaseStatus {
    private String phaseName;
    private double coutPrevu;
    private double coutReel;
    private int delaiPrevu;
    private int delaiReel;
    private boolean depassementCout;
    private boolean depassementDelai;

    // Constructeur
    public PhaseStatus(String phaseName, double coutPrevu, double coutReel, int delaiPrevu, int delaiReel) {
        this.phaseName = phaseName;
        this.coutPrevu = coutPrevu;
        this.coutReel = coutReel;
        this.delaiPrevu = delaiPrevu;
        this.delaiReel = delaiReel;
        this.depassementCout = coutReel > coutPrevu;
        this.depassementDelai = delaiReel > delaiPrevu;
    }

    // Getters
    public String getPhaseName() { return phaseName; }
    public double getCoutPrevu() { return coutPrevu; }
    public double getCoutReel() { return coutReel; }
    public int getDelaiPrevu() { return delaiPrevu; }
    public int getDelaiReel() { return delaiReel; }
    public boolean isDepassementCout() { return depassementCout; }
    public boolean isDepassementDelai() { return depassementDelai; }
}
