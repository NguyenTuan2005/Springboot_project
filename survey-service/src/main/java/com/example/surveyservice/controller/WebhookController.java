package com.example.surveyservice.controller;

import com.example.surveyservice.exception.WebhookProcessingException;
import com.example.surveyservice.service.WebhookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks")
@Tag(name = "Webhook", description = "Webhook management APIs")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/typeform")
    public ResponseEntity<String> handleTypeformWebhook(@RequestBody String payload) {
        try {
            log.info("Received Typeform webhook");
            webhookService.processTypeformWebhook(payload);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (WebhookProcessingException e) {
            log.error("Error processing Typeform webhook", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error processing Typeform webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping("/surveymonkey")
    public ResponseEntity<String> handleSurveyMonkeyWebhook(@RequestBody String payload) {
        try {
            log.info("Received SurveyMonkey webhook");
            webhookService.processSurveyMonkeyWebhook(payload);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (WebhookProcessingException e) {
            log.error("Error processing SurveyMonkey webhook", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error processing SurveyMonkey webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping
    public ResponseEntity<String> handleGenericWebhook(@RequestBody Map<String, Object> payload) {
        try {
            log.info("Received generic webhook");
            webhookService.processWebhook(payload);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (WebhookProcessingException e) {
            log.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}

