package com.prjt2cs.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("com.prjt2cs.project.model")
@SpringBootApplication
public class ForageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForageApplication.class, args);
    }

}
