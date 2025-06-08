package com.prjt2cs.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "actual")
public class Actual {

    @Id
    private Long id; // id unique de la prévision globale

    // Liste des 4 phases prévisions (attention : normalement @OneToMany est sur une
    // collection liée)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ActualPhase> actualPhases;

    public Actual() {
    }

    public Actual(Long id) {
        this.id = id;
    }

    // getters et setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ActualPhase> getActualPhase() {
        return actualPhases;
    }

    public void setActualPhase(List<ActualPhase> actualPhases) {
        this.actualPhases = actualPhases;
    }
}
