package com.example.surveyservice.controller;

import com.example.shared.annotation.Loggable;
import com.example.surveyservice.model.Survey;
import com.example.surveyservice.model.SurveyResponse;
import com.example.surveyservice.request.CreateSurveyResponseRequest;
import com.example.surveyservice.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/surveys")
@Tag(name = "Survey", description = "Survey management APIs")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @PostMapping("/{surveyId}/responses")
    @Operation(summary = "create a survey response")
    @Loggable
    public ResponseEntity<SurveyResponse> createSurveyResponse(
            @PathVariable Long surveyId,
            @RequestBody CreateSurveyResponseRequest createSurveyResponseRequest) {
        try {
            SurveyResponse savedResponse = surveyService.createSurveyResponse(surveyId, createSurveyResponseRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/responses")
    @Operation(summary = "Get all survey responses")
    public ResponseEntity<List<SurveyResponse>> getAllResponses() {
        List<SurveyResponse> responses = surveyService.getAllSurveyResponses();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get all survey response for a specific test")
    @GetMapping("/{surveyId}/responses")
    public ResponseEntity<List<SurveyResponse>> getSurveyResponses(@PathVariable Long surveyId) {
        List<SurveyResponse> responses = surveyService.getSurveyResponses(surveyId);
        return ResponseEntity.ok(responses);
    }


    @Operation(summary = "Get paginated survey responses",
            description = "Retrieve a paginated list of responses for a specific survey.")
    @GetMapping("/{surveyId}/responses?page={page}")
    public ResponseEntity<Page<SurveyResponse>> getSurveyResponsesPaginated(
            @PathVariable Long surveyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Pageable pageable = PageRequest.of(page, size,
                sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending());

        Page<SurveyResponse> responses = surveyService.getSurveyResponsesPaginated(surveyId, pageable);

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @Operation(summary = "Create a new survey")
    @Loggable
    public ResponseEntity<Survey> createSurvey(@RequestBody Survey survey) {
        Survey createSurvey = surveyService.createSurvey(survey);
        return ResponseEntity.status(HttpStatus.CREATED).body(createSurvey);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a survey by ID")
    public ResponseEntity<Survey> getSurveyById(@PathVariable Long id) {
        return surveyService.getSurveyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all survey")
    public ResponseEntity<List<Survey>> getAllSurveys() {
        List<Survey> responses = surveyService.getAllSurveys();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a survey")
    public ResponseEntity<Survey> updateSurvey(@PathVariable Long id, @RequestBody Survey Survey) {
        return surveyService.updateSurvey(id, Survey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a survey")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
        if(surveyService.deleteSurvey(id))
            return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<List<Survey>> getSurveysByTargetIndustry(String targetIndustry) {
        return ResponseEntity.ok(surveyService.getSurveysByTargetIndustry(targetIndustry));
    }
}

