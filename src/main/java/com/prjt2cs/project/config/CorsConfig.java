package com.prjt2cs.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000") // autorise React
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // AJOUT DE PATCH
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
