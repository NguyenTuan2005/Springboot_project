package com.example.analyticsservice.controller;

import com.example.shared.model.SurveyAnalyticsLog;
import com.example.analyticsservice.model.SurveyMetrics;
import com.example.analyticsservice.repository.SurveyMetricsRepository;
import com.example.analyticsservice.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@Tag(name = "Analytics", description = "Analytics management APIs")
public class AnalyticsController {

    private final SurveyMetricsRepository surveyMetricsRepository;
    private final AnalyticsService analyticsService;

    public AnalyticsController(SurveyMetricsRepository surveyMetricsRepository, AnalyticsService analyticsService) {
        this.surveyMetricsRepository = surveyMetricsRepository;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/metrics")
    @Operation(summary = "Get survey metrics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved metrics"),
        @ApiResponse(responseCode = "404", description = "Metrics not found")
    })
    public ResponseEntity<SurveyMetrics> getMetrics() {
        SurveyMetrics metrics = surveyMetricsRepository.findById(1L)
                .orElse(new SurveyMetrics());
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/logs")
    @Operation(summary = "Get all survey analytics logs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved logs"),
        @ApiResponse(responseCode = "404", description = "Logs not found")
    })
    public ResponseEntity<List<SurveyAnalyticsLog>> getAllLogs() {
        List<SurveyAnalyticsLog> logs = analyticsService.getAllLogs();
        return ResponseEntity.ok(logs);
    }
}

