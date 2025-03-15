package com.example.analyticsservice.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
public class JpaConfig {

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(
            HibernateJpaVendorAdapter jpaVendorAdapter,
            ObjectProvider<PersistenceUnitManager> persistenceUnitManager) {
        return new EntityManagerFactoryBuilder(jpaVendorAdapter, new HashMap<>(), persistenceUnitManager.getIfAvailable());
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.example.analyticsservice.model", "com.example.shared.model")
                .build();
    }
}
