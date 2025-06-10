package com.prjt2cs.project.dto;

public class LoginRequest {
    private String email;  // Changé de "username" à "email"
    private String password;

    // Getters & Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}