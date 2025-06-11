package com.prjt2cs.project.controller;

import com.prjt2cs.project.dto.PhaseStatus;
import com.prjt2cs.project.model.PhasePrevision;
import com.prjt2cs.project.repository.PhasePrevisionRepository;
import com.prjt2cs.project.service.ExcelReader;
import com.prjt2cs.project.service.MonitoringService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/previsions")
public class PrevisionController {

    @Autowired
    private PhasePrevisionRepository phasePrevisionRepository;

    @Autowired
    private ExcelReader excelReader;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPrevisions(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Le fichier est vide.");
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                return ResponseEntity.badRequest().body("Le fichier doit être au format Excel (.xlsx ou .xls).");
            }

            System.out.println("Début du traitement du fichier: " + fileName);

            // Traitement des feuilles 0 à 3
            for (int sheetIndex = 0; sheetIndex < 4; sheetIndex++) {
                try {
                    PhasePrevision phase = new PhasePrevision(String.valueOf(sheetIndex));

                    // Set phase name based on sheet index
                    String phaseName = switch (sheetIndex) {
                        case 0 -> "26\"";
                        case 1 -> "16\"";
                        case 2 -> "12\"1/4";
                        case 3 -> "8\"1/2";
                        default -> String.valueOf(sheetIndex);
                    };
                    phase.setPhaseName(phaseName);

                    // Créer un nouveau InputStream pour chaque lecture
                    phase.setDrilling(parseDoubleOrZero(readCellFromFile(file, "B", 2, sheetIndex)));
                    phase.setMudLogging(parseDoubleOrZero(readCellFromFile(file, "B", 3, sheetIndex)));
                    phase.setCementing(parseDoubleOrZero(readCellFromFile(file, "B", 4, sheetIndex)));
                    phase.setWaterSupply(parseDoubleOrZero(readCellFromFile(file, "B", 5, sheetIndex)));
                    phase.setWellHead(parseDoubleOrZero(readCellFromFile(file, "B", 6, sheetIndex)));
                    phase.setCasingTubing(parseDoubleOrZero(readCellFromFile(file, "B", 7, sheetIndex)));
                    phase.setCasingAccessoire(parseDoubleOrZero(readCellFromFile(file, "B", 8, sheetIndex)));
                    phase.setRunCasing(parseDoubleOrZero(readCellFromFile(file, "B", 9, sheetIndex)));
                    phase.setDrillingBit(parseDoubleOrZero(readCellFromFile(file, "B", 10, sheetIndex)));
                    phase.setCorring(parseDoubleOrZero(readCellFromFile(file, "B", 11, sheetIndex)));
                    phase.setDrillingMud(parseDoubleOrZero(readCellFromFile(file, "B", 12, sheetIndex)));
                    phase.setLogging(parseDoubleOrZero(readCellFromFile(file, "B", 13, sheetIndex)));
                    phase.setTesting(parseDoubleOrZero(readCellFromFile(file, "B", 14, sheetIndex)));
                    phase.setSecurite(parseDoubleOrZero(readCellFromFile(file, "B", 15, sheetIndex)));
                    phase.setTotal(parseDoubleOrZero(readCellFromFile(file, "B", 16, sheetIndex)));

                    // Lecture du délai avec debug
                    int delayRow = switch (sheetIndex) {
                        case 0 -> 5; // Feuille 0: colonne D, ligne 5
                        case 1 -> 7; // Feuille 1: colonne D, ligne 9
                        case 2 -> 12; // Feuille 2: colonne D, ligne 12
                        case 3 -> 16; // Feuille 3: colonne D, ligne 16
                        default -> throw new IllegalStateException("Index de phase inattendu: " + sheetIndex);
                    };

                    // DEBUG: Afficher les informations de lecture
                    System.out.println("=== DEBUG DÉLAI PHASE " + sheetIndex + " ===");
                    System.out.println("Lecture cellule: D" + delayRow + " de la feuille " + sheetIndex);

                    String delayValue = readCellFromFile(file, "D", delayRow, 4);
                    System.out.println("Valeur brute lue: '" + delayValue + "'");

                    Double delay = parseDoubleOrZero(delayValue);
                    System.out.println("Valeur convertie: " + delay);

                    int nombreJours = delay.intValue();
                    System.out.println("Nombre de jours final: " + nombreJours);
                    System.out.println("=====================================");

                    phase.setNombreJours(nombreJours);

                    phasePrevisionRepository.save(phase);
                    System.out.println(
                            "Phase " + sheetIndex + " (" + phaseName + ") enregistrée avec " + nombreJours + " jours.");

                } catch (Exception e) {
                    System.err.println("Erreur dans la phase " + sheetIndex + ": " + e.getMessage());
                    e.printStackTrace();
                    // Continue avec la feuille suivante même en cas d'erreur
                }
            }

            return ResponseEntity.ok("Toutes les phases (0-3) importées avec succès.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erreur globale : " + e.getMessage());
        }
    }

    @Autowired
private MonitoringService monitoringService;

@GetMapping("/etat-par-phase")
public ResponseEntity<List<PhaseStatus>> getEtatParPhase() {
    try {
        return ResponseEntity.ok(monitoringService.getEtatParPhase());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().build();
    }
}


    // Méthode helper pour lire une cellule avec un nouveau stream à chaque fois
    private String readCellFromFile(MultipartFile file, String column, int row, int sheetIndex) {
        try (InputStream inputStream = file.getInputStream()) {
            return excelReader.readCell(inputStream, column, row, sheetIndex);
        } catch (Exception e) {
            System.err.println(
                    "Erreur lecture cellule [" + column + row + "] feuille " + sheetIndex + ": " + e.getMessage());
            return null;
        }
    }

    private Double parseDoubleOrZero(String value) {
        try {
            return (value != null && !value.trim().isEmpty()) ? Double.parseDouble(value.trim()) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @GetMapping("")
    public ResponseEntity<List<PhasePrevision>> getAllPrevisions() {
        try {
            return ResponseEntity.ok(phasePrevisionRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("")
    public ResponseEntity<String> deleteAllPrevisions() {
        try {
            phasePrevisionRepository.deleteAll();
            return ResponseEntity.ok("Toutes les prévisions ont été supprimées.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }


}