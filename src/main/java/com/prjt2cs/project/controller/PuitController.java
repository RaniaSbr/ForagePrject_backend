package com.prjt2cs.project.controller;

import com.prjt2cs.project.model.Puit;
import com.prjt2cs.project.repository.PuitRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/puits")
public class PuitController {

    private final PuitRepository puitRepository;

    public PuitController(PuitRepository puitRepository) {
        this.puitRepository = puitRepository;
    }

    // Endpoint pour créer un nouveau puit
    @PostMapping
    public ResponseEntity<?> createPuit(@RequestBody Puit puit) {
        try {
            // Validation des données
            if (puit.getPuitId() == null || puit.getPuitId().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "L'ID du puit est obligatoire"));
            }

            // Vérifier si le puit existe déjà
            if (puitRepository.existsById(puit.getPuitId())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Un puit avec cet ID existe déjà"));
            }

            // Sauvegarder le nouveau puit
            Puit savedPuit = puitRepository.save(puit);

            return ResponseEntity.ok(savedPuit);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Erreur lors de la création du puit: " + e.getMessage()));
        }
    }

    // Endpoint pour récupérer tous les puits (utile pour le frontend)
    @GetMapping
    public ResponseEntity<List<Puit>> getAllPuits() {
        List<Puit> puits = puitRepository.findAll();
        return ResponseEntity.ok(puits);
    }

    // Endpoint pour récupérer un puit par son ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPuitById(@PathVariable String id) {
        return puitRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint pour mettre à jour un puit
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePuit(@PathVariable String id, @RequestBody Puit puitDetails) {
        try {
            return puitRepository.findById(id)
                    .map(puit -> {
                        puit.setPuitName(puitDetails.getPuitName());
                        puit.setLocation(puitDetails.getLocation());
                        puit.setTotalDepth(puitDetails.getTotalDepth());
                        puit.setStatus(puitDetails.getStatus());
                        Puit updatedPuit = puitRepository.save(puit);
                        return ResponseEntity.ok(updatedPuit);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Erreur lors de la mise à jour du puit: " + e.getMessage()));
        }
    }

    // Endpoint pour supprimer un puit
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePuit(@PathVariable String id) {
        try {
            if (puitRepository.existsById(id)) {
                puitRepository.deleteById(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Erreur lors de la suppression du puit: " + e.getMessage()));
        }
    }
}