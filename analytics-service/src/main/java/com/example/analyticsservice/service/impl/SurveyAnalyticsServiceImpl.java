package com.example.analyticsservice.service.impl;

import com.example.analyticsservice.dto.SurveyResponseDTO;
import com.example.analyticsservice.model.SurveyMetrics;
import com.example.analyticsservice.repository.SurveyMetricsRepository;
import com.example.analyticsservice.service.SurveyAnalyticsService;
import com.example.shared.annotation.Loggable;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SurveyAnalyticsServiceImpl implements SurveyAnalyticsService {
    private final SurveyMetricsRepository metricsRepository;

    public SurveyAnalyticsServiceImpl(SurveyMetricsRepository surveyMetricsRepository) {
        this.metricsRepository = surveyMetricsRepository;
    }

    @Override
    @Transactional
    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    @Loggable
    public void processSurveyResponse(SurveyResponseDTO surveyResponse) {
        updateMetrics(surveyResponse);
    }

    @Override
    public List<SurveyMetrics> getAllMetrics() {
        return metricsRepository.findAll();
    }

    @Override
    public Optional<SurveyMetrics> getMetricsById(Long id) {
        return metricsRepository.findById(id);
    }

    @Override
    public SurveyMetrics createMetrics(SurveyMetrics metrics) {
        return metricsRepository.save(metrics);
    }

    @Override
    public void deleteMetrics(Long id) {
        metricsRepository.deleteById(id);
    }

    private void updateMetrics(SurveyResponseDTO surveyResponse) {
        SurveyMetrics metrics = metricsRepository.findById(1L)
                .orElse(new SurveyMetrics());

        // Update total responses
        metrics.setTotalResponses(metrics.getTotalResponses() + 1);

        // Update average company size
        int currentTotal = (int) metrics.getAverageCompanySize() * (metrics.getTotalResponses() - 1);
        int newTotal = currentTotal + Integer.parseInt(surveyResponse.getCompanySize());
        metrics.setAverageCompanySize((double) newTotal / metrics.getTotalResponses());

        // Update popular industries
        Map<String, Integer> industries = metrics.getPopularIndustries();
        if (industries == null) {
            industries = new HashMap<>();
        }
        String industry = surveyResponse.getSurvey().getTargetIndustry();
        industries.put(industry, industries.getOrDefault(industry, 0) + 1);
        metrics.setPopularIndustries(industries);

        metricsRepository.save(metrics);
    }

    @Transactional
    public void updateIndustryMetrics(String industry) {
        // Get or create survey metrics
        SurveyMetrics metrics = metricsRepository.findAll().stream().findFirst()
                .orElse(new SurveyMetrics());

        metrics.setTotalResponses(metrics.getTotalResponses() + 1);

        // Update industry counts
        Map<String, Integer> industries = metrics.getPopularIndustries();
        if (industries == null) {
            industries = new HashMap<>();
            metrics.setPopularIndustries(industries);
        }

        industries.put(industry, industries.getOrDefault(industry, 0) + 1);

        metricsRepository.save(metrics);
    }
}
