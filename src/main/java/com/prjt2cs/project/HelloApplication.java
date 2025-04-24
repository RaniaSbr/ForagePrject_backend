package com.prjt2cs.project;

import com.prjt2cs.project.utils.ExcelReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HelloApplication {

	public static void main(String[] args) {
		// Lancer l'application Spring Boot
		SpringApplication.run(HelloApplication.class, args);

		// Créer une instance de ExcelReader et appeler la méthode
		ExcelReader excelReader = new ExcelReader();

		// Remplacer par le nom du fichier Excel correct dans le dossier resources
		String fileName = "1.xlsx"; // Changez ceci par le nom réel de votre fichier

		// Lire la plage de cellules de CS29 à DD29 (par exemple, ligne 29)
		// En passant les paramètres correctement
		excelReader.readCellRange(fileName, "CS", "DD", 29);
	}
}
