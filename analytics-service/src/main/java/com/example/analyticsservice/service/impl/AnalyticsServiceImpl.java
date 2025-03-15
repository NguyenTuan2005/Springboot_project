package com.example.analyticsservice.service.impl;

import com.example.analyticsservice.service.AnalyticsService;
import com.example.analyticsservice.service.SurveyAnalyticsService;
import com.example.shared.model.SurveyAnalyticsLog;
import com.example.shared.repository.SurveyAnalyticsLogRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {
    private final MeterRegistry meterRegistry;
    private final SurveyAnalyticsLogRepository logRepository;
    private final SurveyAnalyticsService surveyAnalyticsService;

    public AnalyticsServiceImpl(MeterRegistry meterRegistry, SurveyAnalyticsLogRepository logRepository, SurveyAnalyticsService surveyAnalyticsService) {
        this.meterRegistry = meterRegistry;
        this.logRepository = logRepository;
        this.surveyAnalyticsService = surveyAnalyticsService;
    }

    @Override
    public List<SurveyAnalyticsLog> getAllLogs() {
        return logRepository.findAll();
    }

    @Override
    public Optional<SurveyAnalyticsLog> getLogById(Long id) {
        return logRepository.findById(id);
    }

    @Override
    public SurveyAnalyticsLog createLog(SurveyAnalyticsLog log) {
        if (log.getTimestamp() == null) {
            log.setTimestamp(LocalDateTime.now());
        }
        return logRepository.save(log);
    }

    @Override
    public void deleteLog(Long id) {
        logRepository.deleteById(id);
    }

    @Override
    public List<SurveyAnalyticsLog> getLogsByUserLocation(String location) {
        return logRepository.findByUserLocation(location);
    }

    @Override
    public List<SurveyAnalyticsLog> getLogsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return logRepository.findByTimestampBetween(start, end);
    }

    @Override
    public void trackPageView(String page) {
        meterRegistry.counter("page.views", "page", page).increment();
    }

    @Override
    public void trackSurveyCompletion(String userLocation, long timeTaken, int responseLength) {
        // Update metrics in MeterRegistry
        meterRegistry.counter("survey.completions").increment();
        meterRegistry.timer("survey.completion.time").record(timeTaken, java.util.concurrent.TimeUnit.MILLISECONDS);
        meterRegistry.gauge("survey.response.length", responseLength);

        // Save log entry
        SurveyAnalyticsLog log = new SurveyAnalyticsLog();
        log.setUserLocation(userLocation);
        log.setTimeTaken(timeTaken);
        log.setResponseLength(responseLength);
        log.setTimestamp(LocalDateTime.now());

        logRepository.save(log);
    }

    @Override
    public Map<String, Object> getAnalyticsSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Get total logs count
        long totalLogs = logRepository.count();
        summary.put("totalLogs", totalLogs);

        // Get average time taken
        OptionalDouble avgTimeTaken = logRepository.findAll().stream()
                .mapToLong(SurveyAnalyticsLog::getTimeTaken)
                .average();
        summary.put("averageTimeTaken", avgTimeTaken.orElse(0));

        // Get metrics summary if available
        surveyAnalyticsService.getAllMetrics().stream().findFirst().ifPresent(metrics -> {
            summary.put("totalResponses", metrics.getTotalResponses());
            summary.put("averageCompanySize", metrics.getAverageCompanySize());
            summary.put("popularIndustries", metrics.getPopularIndustries());
        });

        return summary;
    }
}
