package com.example.analyticsservice.repository;

import com.example.analyticsservice.model.SurveyMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyMetricsRepository extends JpaRepository<SurveyMetrics, Long> {
}

