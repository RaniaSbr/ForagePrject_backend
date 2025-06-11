package com.prjt2cs.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

// import java.util.List;
// import java.util.Map;

@Configuration
public class DatabaseConnectionTest {

    @Bean
    public CommandLineRunner testDatabaseConnection(@Autowired JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                System.out.println("=== Test de connexion à la base de données ===");
                String result = jdbcTemplate.queryForObject("SELECT 'Connexion réussie!' FROM dual", String.class);
                System.out.println(result);

                // System.out.println("\n=== Liste des tables dans la base de données ===");
                // List<Map<String, Object>> tables = jdbcTemplate.queryForList("SELECT
                // table_name FROM user_tables");

                // if (tables.isEmpty()) {
                // System.out.println("Aucune table trouvée.");
                // } else {
                // tables.forEach(row -> System.out.println("- " + row.get("TABLE_NAME")));
                // }

                System.out.println("=== Fin du test de connexion ===");
            } catch (Exception e) {
                System.err.println("Erreur lors de la connexion à la base de données :");
                e.printStackTrace();
            }
        };
    }
}
