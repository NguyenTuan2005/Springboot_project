package com.example.surveyservice.service;

import com.example.surveyservice.exception.WebhookProcessingException;

import java.util.Map;

public interface WebhookService {

    /**
     * Process a webhook from Typeform.
     *
     * @param payload the JSON payload as a string
     * @throws WebhookProcessingException if there is an error processing the webhook
     */
    void processTypeformWebhook(String payload) throws WebhookProcessingException;

    /**
     * Process a webhook from SurveyMonkey.
     *
     * @param payload the JSON payload as a string
     * @throws WebhookProcessingException if there is an error processing the webhook
     */
    void processSurveyMonkeyWebhook(String payload) throws WebhookProcessingException;

    /**
     * Process a generic webhook.
     *
     * @param payload the webhook payload as a map
     * @throws WebhookProcessingException if there is an error processing the webhook
     */
    void processWebhook(Map<String, Object> payload) throws WebhookProcessingException;
}

