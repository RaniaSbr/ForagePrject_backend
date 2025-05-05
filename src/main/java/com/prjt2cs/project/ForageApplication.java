package com.prjt2cs.project;

import com.prjt2cs.project.service.ExcelReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;

@EntityScan("com.prjt2cs.project.model")
@SpringBootApplication
public class ForageApplication {

	public static void main(String[] args) {
		// Démarrer l'application Spring Boot et obtenir le contexte
		ApplicationContext context = SpringApplication.run(ForageApplication.class, args);

		// Récupérer le bean ExcelReader depuis le contexte
		ExcelReader excelReader = context.getBean(ExcelReader.class);

		// Exemple de lecture des valeurs concaténées
		String fileName = "18.xlsx"; // Mets bien le nom exact
		String startColumn = "L";
		String endColumn = "T";
		int rowIndex = 6;

		// Appeler la méthode pour lire les cellules concaténées
		String result = excelReader.readCellRangeConcatenated(fileName, startColumn, endColumn, rowIndex);

		// Afficher les valeurs concaténées
		System.out.println("Valeurs concaténées : " + result);
	}
}
