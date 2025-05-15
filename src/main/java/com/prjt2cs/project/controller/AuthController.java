package com.prjt2cs.project.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prjt2cs.project.dto.JwtResponse;
import com.prjt2cs.project.dto.LoginRequest;
import com.prjt2cs.project.dto.SignupRequest;
import com.prjt2cs.project.model.ERole;
import com.prjt2cs.project.model.Role;
import com.prjt2cs.project.model.User;
import com.prjt2cs.project.repository.RoleRepository;
import com.prjt2cs.project.repository.UserRepository;
import com.prjt2cs.project.service.JwtService;
import com.prjt2cs.project.service.UserDetailsImpl;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtService jwtService,
                        UserRepository userRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        // Création utilisateur avec nouveaux attributs
        User user = new User(
            signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            passwordEncoder.encode(signUpRequest.getPassword()),
            signUpRequest.getNom(),
            signUpRequest.getPrenom(),
            signUpRequest.getTelephone(),
            signUpRequest.getWilaya()
        );

        Set<Role> roles = new HashSet<>();
        
        try {
            ERole roleEnum = ERole.valueOf("ROLE_" + signUpRequest.getRole());
            List<Role> roleList = roleRepository.findByName(roleEnum);
            
            if (roleList.isEmpty()) {
                return ResponseEntity.badRequest().body("Error: Role not found in database");
            }
            
            roles.add(roleList.get(0));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: Invalid role specified");
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        UserDetailsImpl userDetails = UserDetailsImpl.build(savedUser);
        String jwt = jwtService.generateToken(userDetails);
        
        return ResponseEntity.ok(new JwtResponse(
            jwt,
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .toList(),
            userDetails.getNom(),
            userDetails.getPrenom(),
            userDetails.getTelephone(),
            userDetails.getWilaya()));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {

            User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(401).body("Invalid credentials");
            }

            UserDetailsImpl userDetails = UserDetailsImpl.build(user);
            String jwt = jwtService.generateToken(userDetails);
    
            return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .toList(),
                userDetails.getNom(),
                userDetails.getPrenom(),
                userDetails.getTelephone(),
                userDetails.getWilaya()));
    
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Authentication failed: " + e.getMessage());
        }
    }
}