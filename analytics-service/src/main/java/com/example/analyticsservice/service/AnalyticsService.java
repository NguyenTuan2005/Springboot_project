package com.example.analyticsservice.service;

import com.example.shared.model.SurveyAnalyticsLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AnalyticsService {
    // SurveyAnalyticsLog methods
    List<SurveyAnalyticsLog> getAllLogs();

    Optional<SurveyAnalyticsLog> getLogById(Long id);

    SurveyAnalyticsLog createLog(SurveyAnalyticsLog log);

    void deleteLog(Long id);

    List<SurveyAnalyticsLog> getLogsByUserLocation(String location);

    List<SurveyAnalyticsLog> getLogsByTimeRange(LocalDateTime start, LocalDateTime end);

    // Analytics specific methods
    void trackPageView(String page);

    void trackSurveyCompletion(String userLocation, long timeTaken, int responseLength);

    Map<String, Object> getAnalyticsSummary();
}

