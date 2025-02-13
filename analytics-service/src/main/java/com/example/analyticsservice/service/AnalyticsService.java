package com.example.analyticsservice.service;

import com.example.analyticsservice.model.SurveyAnalyticsLog;
import com.example.analyticsservice.repository.SurveyAnalyticsLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    private final SurveyAnalyticsLogRepository logRepository;

    public AnalyticsService(SurveyAnalyticsLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public List<SurveyAnalyticsLog> getAllLogs() {
        return logRepository.findAll();
    }
}

