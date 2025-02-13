package com.example.analyticsservice.repository;

import com.example.analyticsservice.model.SurveyAnalyticsLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyAnalyticsLogRepository extends JpaRepository<SurveyAnalyticsLog, Long> {
}

