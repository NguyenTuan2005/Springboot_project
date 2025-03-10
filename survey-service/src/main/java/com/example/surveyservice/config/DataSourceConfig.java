package com.example.surveyservice.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/insights_db?stringtype=unspecified");
        dataSource.setUsername("postgres");
        dataSource.setPassword("1111");
        dataSource.setDriverClassName("org.postgresql.Driver");
        return dataSource;
    }
}

