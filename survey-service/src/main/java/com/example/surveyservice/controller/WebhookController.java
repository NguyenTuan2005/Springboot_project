package com.example.surveyservice.controller;

import com.example.surveyservice.service.WebhookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/webhooks")
@Tag(name = "Webhook", description = "Webhook management APIs")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/typeform")
    @Operation(summary = "Handle Typeform webhook", description = "Handles incoming Typeform webhooks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Webhook received successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payload"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> handleTypeformWebhook(@RequestBody String payload) {
        webhookService.processTypeformWebhook(payload);
        return ResponseEntity.ok("Webhook received");
    }

    @PostMapping("/surveymonkey")
    @Operation(summary = "Handle SurveyMonkey webhook", description = "Handles incoming SurveyMonkey webhooks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Webhook received successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payload"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> handleSurveyMonkeyWebhook(@RequestBody String payload) {
        webhookService.processSurveyMonkeyWebhook(payload);
        return ResponseEntity.ok("Webhook received");
    }
}

