package com.example.surveyservice.controller;

import com.example.shared.annotation.Loggable;
import com.example.surveyservice.model.SurveyResponse;
import com.example.surveyservice.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/surveys")
@Tag(name = "Survey", description = "Survey management APIs")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit a survey response")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Survey response submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid survey response"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Loggable
    public ResponseEntity<SurveyResponse> submitSurvey(@RequestBody SurveyResponse surveyResponse) {
        SurveyResponse savedResponse = surveyService.saveSurveyResponse(surveyResponse);
        return ResponseEntity.ok(savedResponse);
    }

    @GetMapping("/responses")
    @Operation(summary = "Get all survey responses")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all survey responses"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<SurveyResponse>> getAllResponses() {
        List<SurveyResponse> responses = surveyService.getAllSurveyResponses();
        return ResponseEntity.ok(responses);
    }
}

