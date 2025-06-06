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

	}
}
