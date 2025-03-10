package com.example.surveyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.example.shared.model", "com.example.surveyservice.model"})
@EnableJpaRepositories(basePackages = {"com.example.shared.repository", "com.example.surveyservice.repository"},
        entityManagerFactoryRef = "entityManagerFactory")
@ComponentScan(basePackages = {
        "com.example.surveyservice",
        "com.example.shared"
})
public class SurveyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SurveyServiceApplication.class, args);
    }
}
