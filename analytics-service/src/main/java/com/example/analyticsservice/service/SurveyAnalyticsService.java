package com.example.analyticsservice.service;

import com.example.analyticsservice.dto.SurveyResponseDTO;
import com.example.analyticsservice.model.SurveyMetrics;

import java.util.List;
import java.util.Optional;

public interface SurveyAnalyticsService {

    void processSurveyResponse(SurveyResponseDTO surveyResponse);

    List<SurveyMetrics> getAllMetrics();

    Optional<SurveyMetrics> getMetricsById(Long id);

    SurveyMetrics createMetrics(SurveyMetrics metrics);

    void deleteMetrics(Long id);

    void updateIndustryMetrics(String industry);

}

