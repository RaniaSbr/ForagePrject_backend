package com.prjt2cs.project.dto;

import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    
    // Ajout des nouveaux attributs
    private String nom;
    private String prenom;
    private String telephone;
    private String wilaya;

    // Constructeur mis à jour
    public JwtResponse(String token, Long id, String username, String email, List<String> roles, 
            String nom, String prenom, String telephone, String wilaya) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.wilaya = wilaya;
    }

    // Getters existants
    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }
    
    // Nouveaux getters pour les attributs ajoutés
    public String getNom() {
        return nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public String getWilaya() {
        return wilaya;
    }
}