package com.example.analyticsservice.service;

import com.example.analyticsservice.dto.SurveyResponseDTO;
import com.example.analyticsservice.model.SurveyMetrics;
import com.example.analyticsservice.repository.SurveyMetricsRepository;
import com.example.shared.annotation.Loggable;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class SurveyAnalyticsService {

    private final SurveyMetricsRepository surveyMetricsRepository;

    public SurveyAnalyticsService(SurveyMetricsRepository surveyMetricsRepository) {
        this.surveyMetricsRepository = surveyMetricsRepository;
    }

    @Transactional
    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    @Loggable
    public void processSurveyResponse(SurveyResponseDTO surveyResponse) {
        // Process the survey response and update metrics
        updateMetrics(surveyResponse);
    }

    private void updateMetrics(SurveyResponseDTO surveyResponse) {
        SurveyMetrics metrics = surveyMetricsRepository.findById(1L)
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

        surveyMetricsRepository.save(metrics);
    }
}

