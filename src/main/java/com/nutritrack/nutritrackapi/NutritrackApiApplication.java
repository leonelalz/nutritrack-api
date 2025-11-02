package com.nutritrack.nutritrackapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.nutritrack.nutritrackapi.model")
@EnableJpaRepositories(basePackages = "com.nutritrack.nutritrackapi.repository")
public class NutritrackApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NutritrackApiApplication.class, args);
    }

}
