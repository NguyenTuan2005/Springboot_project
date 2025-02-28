package com.example.analyticsservice.service;

import com.example.shared.model.SurveyAnalyticsLog;
import com.example.shared.repository.SurveyAnalyticsLogRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    private final MeterRegistry meterRegistry;

    private final SurveyAnalyticsLogRepository logRepository;

    public AnalyticsService(MeterRegistry meterRegistry, SurveyAnalyticsLogRepository logRepository) {
        this.meterRegistry = meterRegistry;
        this.logRepository = logRepository;
    }

    public List<SurveyAnalyticsLog> getAllLogs() {
        return logRepository.findAll();
    }

    public void trackPageView(String page) {
        meterRegistry.counter("page.views", "page", page).increment();
    }
}

