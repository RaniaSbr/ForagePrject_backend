package com.prjt2cs.project;

import com.prjt2cs.project.model.ERole;
import com.prjt2cs.project.model.Role;
import com.prjt2cs.project.repository.RoleRepository;
import com.prjt2cs.project.service.ExcelReader;

import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

@EntityScan("com.prjt2cs.project.model")
@SpringBootApplication
public class ForageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForageApplication.class, args);
    }

    // Initialisation des rôles en base
    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            Stream.of(ERole.values()).forEach(role -> {
                if (roleRepository.findByName(role).isEmpty()) {
                    Role newRole = new Role();
                    newRole.setName(role);
                    roleRepository.save(newRole);
                    System.out.println("Création du rôle : " + role);
                }
            });
        };
    }

    // Exécution de la lecture Excel au démarrage
    @Bean
    public CommandLineRunner runExcelReader(ExcelReader excelReader) {
        return args -> {
            String fileName = "18.xlsx"; // Mets bien le nom exact
            String startColumn = "L";
            String endColumn = "T";
            int rowIndex = 6;

            try {
                String result = excelReader.readCellRangeConcatenated(fileName, startColumn, endColumn, rowIndex, fileName);
                System.out.println("Valeurs concaténées : " + result);
            } catch (Exception e) {
                System.err.println("Erreur lors de la lecture du fichier Excel : " + e.getMessage());
            }
        };
    }
}
