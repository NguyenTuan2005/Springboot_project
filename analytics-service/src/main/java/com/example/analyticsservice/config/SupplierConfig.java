package com.example.analyticsservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class SupplierConfig {
    @Bean
    public Supplier<ProcessBuilder> processBuilderSupplier() {
        return ProcessBuilder::new; // Provides a new ProcessBuilder instance when needed
    }
}
