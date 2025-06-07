package com.prjt2cs.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "prevision")
public class Prevision {

    @Id
    private Long id; // id unique de la prévision globale

    // Liste des 4 phases prévisions (attention : normalement @OneToMany est sur une
    // collection liée)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PhasePrevision> phasePrevisions;

    public Prevision() {
    }

    public Prevision(Long id) {
        this.id = id;
    }

    // getters et setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<PhasePrevision> getPhasePrevisions() {
        return phasePrevisions;
    }

    public void setPhasePrevisions(List<PhasePrevision> phasePrevisions) {
        this.phasePrevisions = phasePrevisions;
    }
}
