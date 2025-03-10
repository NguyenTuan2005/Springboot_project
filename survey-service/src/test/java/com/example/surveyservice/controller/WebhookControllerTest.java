package com.example.surveyservice.controller;

import com.example.surveyservice.exception.WebhookProcessingException;
import com.example.surveyservice.service.WebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WebhookControllerTest {

    @Mock
    private WebhookService webhookService;

    @InjectMocks
    private WebhookController webhookController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(webhookController).build();
    }

    @Nested
    @DisplayName("Typeform Webhook Tests")
    class TypeformWebhookTests {

        @Test
        @DisplayName("Should process Typeform webhook successfully")
        void shouldProcessTypeformWebhookSuccessfully() {
            // Arrange
            String payload = "{\"event_id\": \"abc123\", \"form_response\": {\"form_id\": \"XYZ\"}}";
            doNothing().when(webhookService).processTypeformWebhook(anyString());

            // Act
            ResponseEntity<String> response = webhookController.handleTypeformWebhook(payload);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Webhook processed successfully", response.getBody());
            verify(webhookService, times(1)).processTypeformWebhook(payload);
        }

        @Test
        @DisplayName("Should handle WebhookProcessingException for Typeform webhook")
        void shouldHandleWebhookProcessingExceptionForTypeform() {
            // Arrange
            String payload = "{\"event_id\": \"abc123\", \"form_response\": {\"form_id\": \"XYZ\"}}";
            String errorMessage = "Invalid Typeform payload";
            doThrow(new WebhookProcessingException(errorMessage))
                    .when(webhookService).processTypeformWebhook(anyString());

            // Act
            ResponseEntity<String> response = webhookController.handleTypeformWebhook(payload);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(errorMessage, response.getBody());
            verify(webhookService, times(1)).processTypeformWebhook(payload);
        }

        @Test
        @DisplayName("Should handle unexpected exceptions for Typeform webhook")
        void shouldHandleUnexpectedExceptionForTypeform() {
            // Arrange
            String payload = "{\"event_id\": \"abc123\", \"form_response\": {\"form_id\": \"XYZ\"}}";
            doThrow(new RuntimeException("Database connection failed"))
                    .when(webhookService).processTypeformWebhook(anyString());

            // Act
            ResponseEntity<String> response = webhookController.handleTypeformWebhook(payload);

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("An unexpected error occurred", response.getBody());
            verify(webhookService, times(1)).processTypeformWebhook(payload);
        }

        @Test
        @DisplayName("Should process Typeform webhook with MockMvc")
        void shouldProcessTypeformWebhookWithMockMvc() throws Exception {
            // Arrange
            String payload = "{\"event_id\": \"abc123\", \"form_response\": {\"form_id\": \"XYZ\"}}";
            doNothing().when(webhookService).processTypeformWebhook(anyString());

            // Act & Assert
            mockMvc.perform(post("/api/v1/webhooks/typeform")
                            .contentType("application/json")
                            .content(payload))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Webhook processed successfully"));

            verify(webhookService, times(1)).processTypeformWebhook(payload);
        }
    }

    @Nested
    @DisplayName("SurveyMonkey Webhook Tests")
    class SurveyMonkeyWebhookTests {

        @Test
        @DisplayName("Should process SurveyMonkey webhook successfully")
        void shouldProcessSurveyMonkeyWebhookSuccessfully() {
            // Arrange
            String payload = "{\"event_type\": \"response_completed\", \"object_id\": \"123456\"}";
            doNothing().when(webhookService).processSurveyMonkeyWebhook(anyString());

            // Act
            ResponseEntity<String> response = webhookController.handleSurveyMonkeyWebhook(payload);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Webhook processed successfully", response.getBody());
            verify(webhookService, times(1)).processSurveyMonkeyWebhook(payload);
        }

        @Test
        @DisplayName("Should handle WebhookProcessingException for SurveyMonkey webhook")
        void shouldHandleWebhookProcessingExceptionForSurveyMonkey() {
            // Arrange
            String payload = "{\"event_type\": \"response_completed\", \"object_id\": \"123456\"}";
            String errorMessage = "Invalid SurveyMonkey payload";
            doThrow(new WebhookProcessingException(errorMessage))
                    .when(webhookService).processSurveyMonkeyWebhook(anyString());

            // Act
            ResponseEntity<String> response = webhookController.handleSurveyMonkeyWebhook(payload);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(errorMessage, response.getBody());
            verify(webhookService, times(1)).processSurveyMonkeyWebhook(payload);
        }

        @Test
        @DisplayName("Should handle unexpected exceptions for SurveyMonkey webhook")
        void shouldHandleUnexpectedExceptionForSurveyMonkey() {
            // Arrange
            String payload = "{\"event_type\": \"response_completed\", \"object_id\": \"123456\"}";
            doThrow(new RuntimeException("Service unavailable"))
                    .when(webhookService).processSurveyMonkeyWebhook(anyString());

            // Act
            ResponseEntity<String> response = webhookController.handleSurveyMonkeyWebhook(payload);

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("An unexpected error occurred", response.getBody());
            verify(webhookService, times(1)).processSurveyMonkeyWebhook(payload);
        }

        @Test
        @DisplayName("Should process SurveyMonkey webhook with MockMvc")
        void shouldProcessSurveyMonkeyWebhookWithMockMvc() throws Exception {
            // Arrange
            String payload = "{\"event_type\": \"response_completed\", \"object_id\": \"123456\"}";
            doNothing().when(webhookService).processSurveyMonkeyWebhook(anyString());

            // Act & Assert
            mockMvc.perform(post("/api/v1/webhooks/surveymonkey")
                            .contentType("application/json")
                            .content(payload))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Webhook processed successfully"));

            verify(webhookService, times(1)).processSurveyMonkeyWebhook(payload);
        }
    }

    @Nested
    @DisplayName("Generic Webhook Tests")
    class GenericWebhookTests {

        @Test
        @DisplayName("Should process generic webhook successfully")
        void shouldProcessGenericWebhookSuccessfully() {
            // Arrange
            Map<String, Object> payload = new HashMap<>();
            payload.put("source", "custom-app");
            payload.put("action", "user.created");

            doNothing().when(webhookService).processWebhook(any());

            // Act
            ResponseEntity<String> response = webhookController.handleGenericWebhook(payload);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Webhook processed successfully", response.getBody());
            verify(webhookService, times(1)).processWebhook(payload);
        }

        @Test
        @DisplayName("Should handle WebhookProcessingException for generic webhook")
        void shouldHandleWebhookProcessingExceptionForGeneric() {
            // Arrange
            Map<String, Object> payload = new HashMap<>();
            payload.put("source", "custom-app");
            payload.put("action", "user.created");

            String errorMessage = "Missing required fields";
            doThrow(new WebhookProcessingException(errorMessage))
                    .when(webhookService).processWebhook(any());

            // Act
            ResponseEntity<String> response = webhookController.handleGenericWebhook(payload);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(errorMessage, response.getBody());
            verify(webhookService, times(1)).processWebhook(payload);
        }

        @Test
        @DisplayName("Should handle unexpected exceptions for generic webhook")
        void shouldHandleUnexpectedExceptionForGeneric() {
            // Arrange
            Map<String, Object> payload = new HashMap<>();
            payload.put("source", "custom-app");
            payload.put("action", "user.created");

            doThrow(new RuntimeException("Service timeout"))
                    .when(webhookService).processWebhook(any());

            // Act
            ResponseEntity<String> response = webhookController.handleGenericWebhook(payload);

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("An unexpected error occurred", response.getBody());
            verify(webhookService, times(1)).processWebhook(payload);
        }

        @Test
        @DisplayName("Should process generic webhook with MockMvc")
        void shouldProcessGenericWebhookWithMockMvc() throws Exception {
            // Arrange
            String payload = "{\"source\": \"custom-app\", \"action\": \"user.created\"}";
            doNothing().when(webhookService).processWebhook(any());

            // Act & Assert
            mockMvc.perform(post("/api/v1/webhooks")
                            .contentType("application/json")
                            .content(payload))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Webhook processed successfully"));

            verify(webhookService, times(1)).processWebhook(any());
        }
    }

    /**
     * Mock implementation of WebhookProcessingException for testing
     */
    static class MockWebhookProcessingException extends RuntimeException {
        public MockWebhookProcessingException(String message) {
            super(message);
        }
    }
}
