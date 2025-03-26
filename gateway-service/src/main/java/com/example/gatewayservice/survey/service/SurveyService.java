package com.example.gatewayservice.survey.service;

import com.example.gatewayservice.survey.WebhookRequest;
import com.example.gatewayservice.survey.WebhookResponse;
import com.example.gatewayservice.survey.dto.CustomerJourneyDTO;
import com.example.gatewayservice.survey.dto.SurveyDTO;
import com.example.gatewayservice.survey.dto.SurveyResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SurveyService {
    Mono<SurveyDTO> getSurveyById(String id);
    Flux<SurveyDTO> getAllSurveys();
    Mono<SurveyDTO> createSurvey(SurveyDTO survey);
    Mono<SurveyDTO> updateSurvey(String id, SurveyDTO survey);
    Mono<Void> deleteSurvey(String id);
    Flux<SurveyResponseDTO> getSurveyResponses(String surveyId);
    Mono<SurveyResponseDTO> createSurveyResponse(String surveyId, SurveyResponseDTO response);
    Flux<SurveyResponseDTO> getPaginatedResponses(String surveyId, int page);
    Flux<SurveyResponseDTO> getAllSurveyResponses();

    // Webhook methods
    Mono<WebhookResponse> handleGenericWebhook(WebhookRequest request);
    Mono<WebhookResponse> handleTypeformWebhook(WebhookRequest request);
    Mono<WebhookResponse> handleSurveyMonkeyWebhook(WebhookRequest request);

    // Journey methods
    Mono<CustomerJourneyDTO> addJourneyTouchpoint(CustomerJourneyDTO journey);
    Mono<CustomerJourneyDTO> getJourneyBySurveyId(String surveyId);
}
