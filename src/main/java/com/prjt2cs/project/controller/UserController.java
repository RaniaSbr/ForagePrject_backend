package com.prjt2cs.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.prjt2cs.project.dto.PasswordUpdateRequest;
import com.prjt2cs.project.dto.UserProfileRequest;
import com.prjt2cs.project.model.User;
import com.prjt2cs.project.repository.UserRepository;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

   
     @GetMapping("/profile")
     public ResponseEntity<?> getUserProfile(@RequestParam String username) {
         User user = userRepository.findByUsername(username)
                 .orElseThrow(() -> new RuntimeException("User not found"));
         return ResponseEntity.ok(user);
     }

     @PutMapping("/profile")
     public ResponseEntity<?> updateUserProfile(@RequestBody UserProfileRequest profileRequest) {
         User user = userRepository.findByUsername(profileRequest.getUsername())
                 .orElseThrow(() -> new RuntimeException("User not found"));
 
         if (profileRequest.getEmail() != null && !profileRequest.getEmail().equals(user.getEmail())) {
             if (userRepository.existsByEmail(profileRequest.getEmail())) {
                 return ResponseEntity.badRequest().body("Error: Email is already in use!");
             }
             user.setEmail(profileRequest.getEmail());
         }
 
         if (profileRequest.getNom() != null) user.setNom(profileRequest.getNom());
         if (profileRequest.getPrenom() != null) user.setPrenom(profileRequest.getPrenom());
         if (profileRequest.getTelephone() != null) user.setTelephone(profileRequest.getTelephone());
         if (profileRequest.getWilaya() != null) user.setWilaya(profileRequest.getWilaya());
 
         User updatedUser = userRepository.save(user);
 
         return ResponseEntity.ok(updatedUser);
     }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequest passwordRequest) {
        // Récupérer l'utilisateur directement par le nom d'utilisateur
        User user = userRepository.findByUsername(passwordRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Vérifier que l'ancien mot de passe est correct
        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(400).body("Current password is incorrect");
        }
        
        // Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepository.save(user);
        
        return ResponseEntity.ok("Password updated successfully");
    }
}