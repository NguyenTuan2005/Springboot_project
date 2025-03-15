package com.example.shared.repository;

import com.example.shared.model.SurveyAnalyticsLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SurveyAnalyticsLogRepository extends JpaRepository<SurveyAnalyticsLog, Long> {
    List<SurveyAnalyticsLog> findByUserLocation(String location);

    List<SurveyAnalyticsLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}

