package com.example.analyticsservice.service;

import com.example.analyticsservice.model.SurveyMetrics;
import com.example.analyticsservice.model.SurveyResponse;
import com.example.analyticsservice.repository.SurveyMetricsRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SurveyAnalyticsService {

    @Autowired
    private SurveyMetricsRepository surveyMetricsRepository;

    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void processSurveyResponse(SurveyResponse surveyResponse) {
        // Process the survey response and update metrics
        updateMetrics(surveyResponse);
    }

    private void updateMetrics(SurveyResponse surveyResponse) {
        SurveyMetrics metrics = surveyMetricsRepository.findById(1L)
                .orElse(new SurveyMetrics());

        // Update total responses
        metrics.setTotalResponses(metrics.getTotalResponses() + 1);

        // Update average company size
        int currentTotal = metrics.getAverageCompanySize() * (metrics.getTotalResponses() - 1);
        int newTotal = currentTotal + Integer.parseInt(surveyResponse.getCompanySize());
        metrics.setAverageCompanySize(newTotal / metrics.getTotalResponses());

        // Update popular industries
        Map<String, Integer> industries = metrics.getPopularIndustries();
        if (industries == null) {
            industries = new HashMap<>();
        }
        String industry = surveyResponse.getSurvey().getTargetIndustry();
        industries.put(industry, industries.getOrDefault(industry, 0) + 1);
        metrics.setPopularIndustries(industries);

        // Save updated metrics
        surveyMetricsRepository.save(metrics);
    }
}

