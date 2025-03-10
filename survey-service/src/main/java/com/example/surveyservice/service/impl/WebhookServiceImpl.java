package com.example.surveyservice.service.impl;

import com.example.surveyservice.exception.WebhookProcessingException;
import com.example.surveyservice.model.Survey;
import com.example.surveyservice.request.CreateSurveyResponseRequest;
import com.example.surveyservice.service.SurveyService;
import com.example.surveyservice.service.WebhookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WebhookServiceImpl implements WebhookService {
    private final SurveyService surveyService;
    private final ObjectMapper objectMapper;

    public WebhookServiceImpl(SurveyService surveyService, ObjectMapper objectMapper) {
        this.surveyService = surveyService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void processTypeformWebhook(String payload) throws WebhookProcessingException {
        try {
            // Parse the payload
            JsonNode rootNode = parseJsonPayload(payload);
            JsonNode formResponse = getRequiredNode(rootNode, "form_response", "Missing 'form_response' node");

            // Extract form_id
            String formId = getRequiredTextValue(formResponse, "form_id", "Missing 'form_id' in form_response");

            // Get company size if available
            String companySize = getOptionalTextValue(formResponse.path("hidden"), "company_size");

            // Process answers
            JsonNode answersNode = getRequiredNode(formResponse, "answers", "Missing 'answers' in form_response");
            List<Object> answersList = convertNodeToList(answersNode);
            Map<String, Object> answersMap = Collections.singletonMap("answers", answersList);

            // Get survey and create response
            createSurveyResponse(formId, companySize, answersMap);

        } catch (Exception e) {
            log.error("Failed to process Typeform webhook", e);
            throw new WebhookProcessingException("Failed to process Typeform webhook: " + e.getMessage(), e);
        }
    }

    @Override
    public void processSurveyMonkeyWebhook(String payload) throws WebhookProcessingException {
        try {
            // Parse the payload
            JsonNode rootNode = parseJsonPayload(payload);

            // Extract survey_id
            String surveyId = getRequiredTextValue(rootNode, "survey_id", "Missing 'survey_id' in payload");

            // Get company size if available
            String companySize = getOptionalTextValue(rootNode, "company_size");

            // Process questions
            JsonNode questionsNode = getRequiredNode(rootNode, "questions", "Missing 'questions' in payload");
            List<Object> questionsList = convertNodeToList(questionsNode);
            Map<String, Object> answersMap = Collections.singletonMap("questions", questionsList);

            // Get survey and create response
            createSurveyResponse(surveyId, companySize, answersMap);

        } catch (Exception e) {
            log.error("Failed to process SurveyMonkey webhook", e);
            throw new WebhookProcessingException("Failed to process SurveyMonkey webhook: " + e.getMessage(), e);
        }
    }

    @Override
    public void processWebhook(Map<String, Object> payload) throws WebhookProcessingException {
        if (payload == null) {
            throw new WebhookProcessingException("Payload cannot be null");
        }

        try {
            String payloadStr = objectMapper.writeValueAsString(payload);

            if (payload.containsKey("form_response")) {
                processTypeformWebhook(payloadStr);
            } else if (payload.containsKey("survey_id") && payload.containsKey("questions")) {
                processSurveyMonkeyWebhook(payloadStr);
            } else {
                throw new WebhookProcessingException("Unknown webhook payload structure");
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to process webhook payload", e);
            throw new WebhookProcessingException("Failed to process webhook payload: " + e.getMessage(), e);
        }
    }

    /**
     * Parse a JSON string into a JsonNode
     */
    private JsonNode parseJsonPayload(String payload) throws JsonProcessingException {
        if (payload == null || payload.isBlank()) {
            throw new WebhookProcessingException("Payload cannot be null or empty");
        }
        return objectMapper.readTree(payload);
    }

    /**
     * Get a required node from a JsonNode
     */
    private JsonNode getRequiredNode(JsonNode node, String fieldName, String errorMessage) {
        JsonNode childNode = node.path(fieldName);
        if (childNode.isMissingNode() || childNode.isNull()) {
            throw new WebhookProcessingException(errorMessage);
        }
        return childNode;
    }

    /**
     * Get a required text value from a JsonNode
     */
    private String getRequiredTextValue(JsonNode node, String fieldName, String errorMessage) {
        String value = node.path(fieldName).asText(null);
        if (value == null) {
            throw new WebhookProcessingException(errorMessage);
        }
        return value;
    }

    /**
     * Get an optional text value from a JsonNode
     */
    private String getOptionalTextValue(JsonNode node, String fieldName) {
        return node.path(fieldName).asText(null);
    }

    /**
     * Convert a JsonNode to a List
     */
    private List<Object> convertNodeToList(JsonNode node) {
        return objectMapper.convertValue(node, new TypeReference<List<Object>>() {});
    }

    /**
     * Create a survey response
     */
    private void createSurveyResponse(String surveyId, String companySize, Map<String, Object> answersMap) {
        try {
            Long surveyIdLong = Long.parseLong(surveyId);

            Survey survey = surveyService.getSurveyById(surveyIdLong)
                    .orElseThrow(() -> new WebhookProcessingException("Survey not found with ID: " + surveyId));

            CreateSurveyResponseRequest request = CreateSurveyResponseRequest.builder()
                    .companySize(companySize)
                    .answers(answersMap)
                    .build();

            surveyService.createSurveyResponse(survey.getId(), request);

        } catch (NumberFormatException e) {
            throw new WebhookProcessingException("Invalid survey ID format: " + surveyId);
        }
    }
}
