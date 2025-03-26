package com.example.gatewayservice.survey;

import com.example.gatewayservice.config.ViewProperties;
import com.example.gatewayservice.survey.dto.CustomerJourneyDTO;
import com.example.gatewayservice.survey.dto.SurveyDTO;
import com.example.gatewayservice.survey.dto.SurveyResponseDTO;
import com.example.gatewayservice.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;
    private final ViewProperties viewProperties;

    @GetMapping("/surveys")
    public Mono<String> getSurveyPage(Model model) {
        model.addAttribute("activePage", "surveys");
        model.addAttribute("surveys", surveyService.getAllSurveys().collectList().block());
        return Mono.just(viewProperties.getSurveyPrefix() + "survey-service");
    }

    @GetMapping("/webhooks")
    public Mono<String> webhooksPage(Model model) {
        model.addAttribute("activePage", "webhooks");
        return Mono.just(viewProperties.getSurveyPrefix() + "webhook-page");
    }

    @GetMapping("/journeys")
    public Mono<String> journeysPage(Model model) {
        model.addAttribute("activePage", "journeys");
        return Mono.just(viewProperties.getSurveyPrefix() + "journey-page");
    }

    @GetMapping("/surveys/{id}")
    public Mono<String> getSurveyById(@PathVariable String id, Model model) {
        return surveyService.getSurveyById(id).map(survey -> {
            model.addAttribute("survey", survey);
            return viewProperties.getSurveyPrefix() + "survey-detail";
        });
    }

    @PostMapping("/surveys")
    public Mono<String> createSurvey(@ModelAttribute SurveyDTO survey, Model model) {
        return surveyService.createSurvey(survey)
                .map(response -> "redirect:/surveys")
                .onErrorResume(e -> {
                    model.addAttribute("error", e.getMessage());
                    return Mono.just(viewProperties.getSurveyPrefix() + "survey-form");
                });
    }

    @PutMapping("/surveys/{id}")
    public Mono<String> updateSurvey(@PathVariable String id, @ModelAttribute SurveyDTO survey, Model model) {
        return surveyService.updateSurvey(id, survey)
                .map(updatedSurvey -> "redirect:/surveys")
                .onErrorResume(e -> {
                    model.addAttribute("error", e.getMessage());
                    return Mono.just(viewProperties.getSurveyPrefix() + "survey-form");
                });
    }

    @DeleteMapping("/surveys/{id}")
    public Mono<String> deleteSurvey(@PathVariable String id) {
        return surveyService.deleteSurvey(id).thenReturn("redirect:/surveys");
    }

    @GetMapping("/surveys/{surveyId}/responses")
    public Mono<String> getSurveyResponses(@PathVariable String surveyId, Model model) {
        return surveyService.getSurveyResponses(surveyId).collectList().map(responses -> {
            model.addAttribute("responses", responses);
            return viewProperties.getSurveyPrefix() + "survey-responses";
        });
    }

    @PostMapping("/surveys/{surveyId}/responses")
    public Mono<String> createSurveyResponse(@PathVariable String surveyId, @ModelAttribute SurveyResponseDTO response, Model model) {
        return surveyService.createSurveyResponse(surveyId, response)
                .map(res -> "redirect:/surveys/" + surveyId + "/responses")
                .onErrorResume(e -> {
                    model.addAttribute("error", e.getMessage());
                    return Mono.just(viewProperties.getSurveyPrefix() + "survey-responses-form");
                });
    }

    @GetMapping("/surveys/{surveyId}/responses/page/{page}")
    public Mono<String> getPaginatedResponses(@PathVariable String surveyId, @PathVariable int page, Model model) {
        return surveyService.getPaginatedResponses(surveyId, page).collectList().map(responses -> {
            model.addAttribute("responses", responses);
            model.addAttribute("page", page);
            return viewProperties.getSurveyPrefix() + "survey-responses";
        });
    }

    @GetMapping("/surveys/responses")
    public Mono<String> getAllSurveyResponses(Model model) {
        return surveyService.getAllSurveyResponses().collectList().map(responses -> {
            model.addAttribute("responses", responses);
            return viewProperties.getSurveyPrefix() + "survey-responses";
        });
    }

    @PostMapping("/webhooks")
    public Mono<String> handleGenericWebhook(@ModelAttribute WebhookRequest request, Model model) {
        return surveyService.handleGenericWebhook(request).map(response -> "redirect:/webhooks");
    }

    @PostMapping("/webhooks/typeform")
    public Mono<String> handleTypeformWebhook(@ModelAttribute WebhookRequest request, Model model) {
        return surveyService.handleTypeformWebhook(request).map(response -> "redirect:/webhooks");
    }

    @PostMapping("/webhooks/surveymonkey")
    public Mono<String> handleSurveyMonkeyWebhook(@ModelAttribute WebhookRequest request, Model model) {
        return surveyService.handleSurveyMonkeyWebhook(request).map(response -> "redirect:/webhooks");
    }

    @PostMapping("/journey")
    public Mono<String> addJourneyTouchpoint(@ModelAttribute CustomerJourneyDTO journey, Model model) {
        return surveyService.addJourneyTouchpoint(journey).map(res -> "redirect:/journeys");
    }

    @GetMapping("/journey/{surveyId}")
    public Mono<String> getJourneyBySurveyId(@PathVariable String surveyId, Model model) {
        return surveyService.getJourneyBySurveyId(surveyId).map(journey -> {
            model.addAttribute("journey", journey);
            return viewProperties.getSurveyPrefix() + "journey-detail";
        });
    }
}
