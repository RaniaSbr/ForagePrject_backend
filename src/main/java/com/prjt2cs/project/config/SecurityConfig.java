package com.prjt2cs.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import com.prjt2cs.project.filter.JwtAuthFilter;

@Configuration
@EnableWebSecurity

public class SecurityConfig {
    
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Désactive CSRF (obligatoire pour les APIs stateless)
            .csrf(csrf -> csrf.disable())
            
            // Autorise TOUTES les requêtes sans authentification (temporairement)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // ⚠️ À remplacer plus tard par vos règles métier
            )
            
            // Désactive la gestion de session
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}