package com.prjt2cs.project;

import com.prjt2cs.project.service.ExcelReader;
import com.prjt2cs.project.service.MonoReader;

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
		MonoReader monoReader = context.getBean(MonoReader.class);

		// Exemple de lecture des valeurs concaténées
		String fileName = "18.xlsx"; // Mets bien le nom exact
		String startColumn = "I";
		String endColumn = "I";
		int rowIndex = 5;

		// Appeler la méthode pour lire les cellules concaténées
		String result = excelReader.readCellRangeConcatenated(fileName, startColumn, endColumn, rowIndex);
		Double result2 = monoReader.readCalculatedCellAsDouble(fileName, "G", 12, 1);

		// Afficher les valeurs concaténées
		System.out.println("Valeurs concaténées : " + result);
		System.out.println("Valeurs concaténées : " + result2);

	}
}
