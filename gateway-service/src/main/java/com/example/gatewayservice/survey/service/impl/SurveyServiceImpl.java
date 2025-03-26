package com.example.gatewayservice.survey.service.impl;

import com.example.gatewayservice.survey.WebhookRequest;
import com.example.gatewayservice.survey.dto.CustomerJourneyDTO;
import com.example.gatewayservice.survey.dto.SurveyDTO;
import com.example.gatewayservice.survey.dto.SurveyResponseDTO;
import com.example.gatewayservice.survey.service.SurveyService;
import com.example.gatewayservice.survey.WebhookResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class SurveyServiceImpl implements SurveyService {

    private static final String PATH = "/api/v1/";
    private final WebClient webClient;

    public SurveyServiceImpl(WebClient.Builder webClientBuilder,
                             @Value("${survey.service.base-url}") String surveyServiceBaseUrl) {
        this.webClient = webClientBuilder.baseUrl(surveyServiceBaseUrl).build();
    }

    @Override
    public Mono<SurveyDTO> getSurveyById(String id) {
        return webClient.get()
                .uri(PATH + "surveys/{id}", id)
                .retrieve()
                .bodyToMono(SurveyDTO.class);
    }

    @Override
    public Flux<SurveyDTO> getAllSurveys() {
        return webClient.get()
                .uri(PATH + "surveys")
                .retrieve()
                .bodyToFlux(SurveyDTO.class);
    }

    @Override
    public Mono<SurveyDTO> createSurvey(SurveyDTO survey) {
        return webClient.post()
                .uri(PATH + "surveys")
                .bodyValue(survey)
                .retrieve()
                .bodyToMono(SurveyDTO.class);
    }

    @Override
    public Mono<SurveyDTO> updateSurvey(String id, SurveyDTO survey) {
        return webClient.put()
                .uri(PATH + "surveys/{id}", id)
                .bodyValue(survey)
                .retrieve()
                .bodyToMono(SurveyDTO.class);
    }

    @Override
    public Mono<Void> deleteSurvey(String id) {
        return webClient.delete()
                .uri(PATH + "surveys/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Flux<SurveyResponseDTO> getSurveyResponses(String surveyId) {
        return webClient.get()
                .uri(PATH + "surveys/{surveyId}/responses", surveyId)
                .retrieve()
                .bodyToFlux(SurveyResponseDTO.class);
    }

    @Override
    public Mono<SurveyResponseDTO> createSurveyResponse(String surveyId, SurveyResponseDTO response) {
        return webClient.post()
                .uri(PATH + "surveys/{surveyId}/responses", surveyId)
                .bodyValue(response)
                .retrieve()
                .bodyToMono(SurveyResponseDTO.class);
    }

    @Override
    public Flux<SurveyResponseDTO> getPaginatedResponses(String surveyId, int page) {
        return webClient.get()
                .uri(PATH + "surveys/{surveyId}/responses?page={page}", surveyId, page)
                .retrieve()
                .bodyToFlux(SurveyResponseDTO.class);
    }

    @Override
    public Flux<SurveyResponseDTO> getAllSurveyResponses() {
        return webClient.get()
                .uri(PATH + "surveys/responses")
                .retrieve()
                .bodyToFlux(SurveyResponseDTO.class);
    }

    // Webhook methods
    @Override
    public Mono<WebhookResponse> handleGenericWebhook(WebhookRequest request) {
        return webClient.post()
                .uri(PATH.replace("v1", "") + "webhooks")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(WebhookResponse.class);
    }

    @Override
    public Mono<WebhookResponse> handleTypeformWebhook(WebhookRequest request) {
        return webClient.post()
                .uri(PATH.replace("v1", "") + "webhooks/typeform")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(WebhookResponse.class);
    }

    @Override
    public Mono<WebhookResponse> handleSurveyMonkeyWebhook(WebhookRequest request) {
        return webClient.post()
                .uri(PATH.replace("v1", "") + "webhooks/surveymonkey")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(WebhookResponse.class);
    }

    // Journey methods
    @Override
    public Mono<CustomerJourneyDTO> addJourneyTouchpoint(CustomerJourneyDTO journey) {
        return webClient.post()
                .uri(PATH + "journey")
                .bodyValue(journey)
                .retrieve()
                .bodyToMono(CustomerJourneyDTO.class);
    }

    @Override
    public Mono<CustomerJourneyDTO> getJourneyBySurveyId(String surveyId) {
        return webClient.get()
                .uri(PATH + "journey/{surveyId}", surveyId)
                .retrieve()
                .bodyToMono(CustomerJourneyDTO.class);
    }
}