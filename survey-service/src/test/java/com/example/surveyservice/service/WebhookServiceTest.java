package com.example.surveyservice.service;

import com.example.surveyservice.exception.WebhookProcessingException;
import com.example.surveyservice.model.Survey;
import com.example.surveyservice.model.SurveyResponse;
import com.example.surveyservice.request.CreateSurveyResponseRequest;
import com.example.surveyservice.service.impl.WebhookServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "test")
class WebhookServiceTest {

    @Mock
    private SurveyService surveyService;

    private ObjectMapper objectMapper;
    private WebhookService webhookService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        webhookService = new WebhookServiceImpl(surveyService, objectMapper);
    }

    // Typeform webhook tests

    @Test
    void processTypeformWebhook_ValidPayload_Success() throws JsonProcessingException {
        // Arrange
        String formId = "123456";
        String companySize = "50-100";
        Survey survey = new Survey();
        survey.setId(Long.parseLong(formId));

        String payload = createTypeformPayload(formId, companySize);

        when(surveyService.getSurveyById(Long.parseLong(formId))).thenReturn(Optional.of(survey));
        when(surveyService.createSurveyResponse(anyLong(), any())).thenReturn(new SurveyResponse());

        // Act
        webhookService.processTypeformWebhook(payload);

        // Assert
        verify(surveyService).getSurveyById(Long.parseLong(formId));
        verify(surveyService).createSurveyResponse(eq(Long.parseLong(formId)), any(CreateSurveyResponseRequest.class));
    }

    @Test
    void processTypeformWebhook_MissingFormResponse_ThrowsException() {
        // Arrange
        String payload = "{}";

        // Act & Assert
        WebhookProcessingException exception = assertThrows(
                WebhookProcessingException.class,
                () -> webhookService.processTypeformWebhook(payload)
        );

        assertTrue(exception.getMessage().contains("Missing 'form_response' node"));
    }

    @Test
    void processTypeformWebhook_MissingFormId_ThrowsException() throws JsonProcessingException {
        // Arrange
        Map<String, Object> formResponse = new HashMap<>();
        Map<String, Object> payload = Map.of("form_response", formResponse);
        String payloadStr = objectMapper.writeValueAsString(payload);

        // Act & Assert
        WebhookProcessingException exception = assertThrows(
                WebhookProcessingException.class,
                () -> webhookService.processTypeformWebhook(payloadStr)
        );

        assertTrue(exception.getMessage().contains("Missing 'form_id' in form_response"));
    }

    @Test
    void processTypeformWebhook_MissingAnswers_ThrowsException() throws JsonProcessingException {
        // Arrange
        Map<String, Object> formResponse = new HashMap<>();
        formResponse.put("form_id", "123456");
        Map<String, Object> payload = Map.of("form_response", formResponse);
        String payloadStr = objectMapper.writeValueAsString(payload);

        // Act & Assert
        WebhookProcessingException exception = assertThrows(
                WebhookProcessingException.class,
                () -> webhookService.processTypeformWebhook(payloadStr)
        );

        assertTrue(exception.getMessage().contains("Missing 'answers' in form_response"));
    }

    @Test
    void processTypeformWebhook_SurveyNotFound_ThrowsException() throws JsonProcessingException {
        // Arrange
        String formId = "123456";
        String companySize = "50-100";
        String payload = createTypeformPayload(formId, companySize);

        when(surveyService.getSurveyById(Long.parseLong(formId))).thenReturn(Optional.empty());

        // Act & Assert
        WebhookProcessingException exception = assertThrows(
                WebhookProcessingException.class,
                () -> webhookService.processTypeformWebhook(payload)
        );

        assertTrue(exception.getMessage().contains("Survey not found with ID: " + formId));
    }

    @Test
    void processTypeformWebhook_InvalidSurveyId_ThrowsException() throws JsonProcessingException {
        // Arrange
        String formId = "invalid";
        String companySize = "50-100";

        Map<String, Object> hidden = new HashMap<>();
        hidden.put("company_size", companySize);

        Map<String, Object> formResponse = new HashMap<>();
        formResponse.put("form_id", formId);
        formResponse.put("hidden", hidden);
        formResponse.put("answers", List.of());

        Map<String, Object> payload = Map.of("form_response", formResponse);
        String payloadStr = objectMapper.writeValueAsString(payload);

        // Act & Assert
        WebhookProcessingException exception = assertThrows(
                WebhookProcessingException.class,
                () -> webhookService.processTypeformWebhook(payloadStr)
        );

        assertTrue(exception.getMessage().contains("Invalid survey ID format"));
    }

    // SurveyMonkey webhook tests

    @Test
    void processSurveyMonkeyWebhook_ValidPayload_Success() throws JsonProcessingException {
        // Arrange
        String surveyId = "789012";
        String companySize = "100-500";
        Survey survey = new Survey();
        survey.setId(Long.parseLong(surveyId));

        String payload = createSurveyMonkeyPayload(surveyId, companySize);

        when(surveyService.getSurveyById(Long.parseLong(surveyId))).thenReturn(Optional.of(survey));
        when(surveyService.createSurveyResponse(anyLong(), any())).thenReturn(new SurveyResponse());

        // Act
        webhookService.processSurveyMonkeyWebhook(payload);

        // Assert
        verify(surveyService).getSurveyById(Long.parseLong(surveyId));
        verify(surveyService).createSurveyResponse(eq(Long.parseLong(surveyId)), any(CreateSurveyResponseRequest.class));
    }

    @Test
    void processSurveyMonkeyWebhook_MissingSurveyId_ThrowsException() {
        // Arrange
        String payload = "{}";

        // Act & Assert
        WebhookProcessingException exception = assertThrows(
                WebhookProcessingException.class,
                () -> webhookService.processSurveyMonkeyWebhook(payload)
        );

        assertTrue(exception.getMessage().contains("Missing 'survey_id' in payload"));
    }

    @Test
    void processSurveyMonkeyWebhook_MissingQuestions_ThrowsException() throws JsonProcessingException {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("survey_id", "789012");
        String payloadStr = objectMapper.writeValueAsString(payload);

        // Act & Assert
        WebhookProcessingException exception = assertThrows(
                WebhookProcessingException.class,
                () -> webhookService.processSurveyMonkeyWebhook(payloadStr)
        );

        assertTrue(exception.getMessage().contains("Missing 'questions' in payload"));
    }

    @Test
    void processSurveyMonkeyWebhook_SurveyNotFound_ThrowsException() throws JsonProcessingException {
        // Arrange
        String surveyId = "789012";
        String companySize = "100-500";
        String payload = createSurveyMonkeyPayload(surveyId, companySize);

        when(surveyService.getSurveyById(Long.parseLong(surveyId))).thenReturn(Optional.empty());

        // Act & Assert
        WebhookProcessingException exception = assertThrows(
                WebhookProcessingException.class,
                () -> webhookService.processSurveyMonkeyWebhook(payload)
        );

        assertTrue(exception.getMessage().contains("Survey not found with ID: " + surveyId));
    }

    @Test
    void processSurveyMonkeyWebhook_InvalidSurveyId_ThrowsException() throws JsonProcessingException {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("survey_id", "invalid");
        payload.put("questions", List.of());
        String payloadStr = objectMapper.writeValueAsString(payload);

        // Act & Assert
        WebhookProcessingException exception = assertThrows(
                WebhookProcessingException.class,
                () -> webhookService.processSurveyMonkeyWebhook(payloadStr)
        );

        assertTrue(exception.getMessage().contains("Invalid survey ID format"));
    }

    // Generic webhook tests

    @Test
    void processWebhook_TypeformPayload_Success() {
        // Arrange
        Map<String, Object> formResponse = new HashMap<>();
        formResponse.put("form_id", "123456");
        formResponse.put("answers", List.of());

        Map<String, Object> payload = new HashMap<>();
        payload.put("form_response", formResponse);

        Survey survey = new Survey();
        survey.setId(123456L);

        when(surveyService.getSurveyById(123456L)).thenReturn(Optional.of(survey));
        when(surveyService.createSurveyResponse(anyLong(), any())).thenReturn(new SurveyResponse());

        // Act
        webhookService.processWebhook(payload);

        // Assert
        verify(surveyService).getSurveyById(123456L);
        verify(surveyService).createSurveyResponse(eq(123456L), any(CreateSurveyResponseRequest.class));
    }

    @Test
    void processWebhook_SurveyMonkeyPayload_Success() {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("survey_id", "789012");
        payload.put("questions", List.of());

        Survey survey = new Survey();
        survey.setId(789012L);

        when(surveyService.getSurveyById(789012L)).thenReturn(Optional.of(survey));
        when(surveyService.createSurveyResponse(anyLong(), any())).thenReturn(new SurveyResponse());

        // Act
        webhookService.processWebhook(payload);

        // Assert
        verify(surveyService).getSurveyById(789012L);
        verify(surveyService).createSurveyResponse(eq(789012L), any(CreateSurveyResponseRequest.class));
    }

    @Test
    void processWebhook_UnknownPayload_ThrowsException() {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("unknown", "value");

        // Act & Assert
        WebhookProcessingException exception = assertThrows(
                WebhookProcessingException.class,
                () -> webhookService.processWebhook(payload)
        );

        assertTrue(exception.getMessage().contains("Unknown webhook payload structure"));
    }

    @Test
    void processWebhook_NullPayload_ThrowsException() {
        // Act & Assert
        WebhookProcessingException exception = assertThrows(
                WebhookProcessingException.class,
                () -> webhookService.processWebhook(null)
        );

        assertTrue(exception.getMessage().contains("Payload cannot be null"));
    }

    // Helper methods

    private String createTypeformPayload(String formId, String companySize) throws JsonProcessingException {
        Map<String, Object> hidden = new HashMap<>();
        hidden.put("company_size", companySize);

        Map<String, Object> formResponse = new HashMap<>();
        formResponse.put("form_id", formId);
        formResponse.put("hidden", hidden);
        formResponse.put("answers", List.of(Map.of("field_id", "abc", "value", "test")));

        Map<String, Object> payload = Map.of("form_response", formResponse);
        return objectMapper.writeValueAsString(payload);
    }

    private String createSurveyMonkeyPayload(String surveyId, String companySize) throws JsonProcessingException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("survey_id", surveyId);
        payload.put("company_size", companySize);
        payload.put("questions", List.of(Map.of("id", "xyz", "answer", "test")));

        return objectMapper.writeValueAsString(payload);
    }
}