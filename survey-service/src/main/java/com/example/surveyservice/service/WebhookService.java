package com.example.surveyservice.service;

import com.example.surveyservice.model.SurveyResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class WebhookService {

    private final SurveyService surveyService;

    private final ObjectMapper objectMapper;

    public WebhookService(SurveyService surveyService, ObjectMapper objectMapper) {
        this.surveyService = surveyService;
        this.objectMapper = objectMapper;
    }

    public void processTypeformWebhook(String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            // Extract relevant information from the Typeform payload
            // Create a SurveyResponse object and save it
            SurveyResponse response = new SurveyResponse();
            // Set response properties based on the payload
            surveyService.saveSurveyResponse(response);
        } catch (Exception e) {
            // Handle exception
        }
    }

    public void processSurveyMonkeyWebhook(String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            // Extract relevant information from the SurveyMonkey payload
            // Create a SurveyResponse object and save it
            SurveyResponse response = new SurveyResponse();
            // Set response properties based on the payload
            surveyService.saveSurveyResponse(response);
        } catch (Exception e) {
            // Handle exception
        }
    }
}

