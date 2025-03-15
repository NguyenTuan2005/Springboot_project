package com.example.analyticsservice.controller;

import com.example.analyticsservice.service.ReportService;
import com.example.analyticsservice.service.SurveyAnalyticsService;
import com.example.shared.model.SurveyAnalyticsLog;
import com.example.analyticsservice.model.SurveyMetrics;
import com.example.analyticsservice.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("api/v1/analytics")
@Tag(name = "Analytics", description = "Analytics management APIs")
public class AnalyticsController {

    private final SurveyAnalyticsService surveyAnalyticsService;
    private final AnalyticsService analyticsService;
    private final ReportService reportService;

    public AnalyticsController(SurveyAnalyticsService surveyAnalyticsService, AnalyticsService analyticsService, ReportService reportService) {
        this.surveyAnalyticsService = surveyAnalyticsService;
        this.analyticsService = analyticsService;
        this.reportService = reportService;
    }

    @GetMapping("/logs")
    @Operation(summary = "Get all survey analytics logs")
    public ResponseEntity<List<SurveyAnalyticsLog>> getAllLogs() {
        return ResponseEntity.ok(analyticsService.getAllLogs());
    }

    @GetMapping("/logs/{id}")
    @Operation(summary = "Get survey analytics log by ID")
    public ResponseEntity<SurveyAnalyticsLog> getLogById(@PathVariable Long id) {
        return analyticsService.getLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/logs")
    @Operation(summary = "Create new survey analytics log")
    public ResponseEntity<SurveyAnalyticsLog> saveLog(@RequestBody SurveyAnalyticsLog log) {
        return ResponseEntity.status(HttpStatus.CREATED).body(analyticsService.createLog(log));
    }

    @DeleteMapping("/logs/{id}")
    @Operation(summary = "Delete survey analytics log by ID")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        analyticsService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/logs/location/{location}")
    @Operation(summary = "Get logs by user location")
    public ResponseEntity<List<SurveyAnalyticsLog>> getLogsByUserLocation(@PathVariable String location) {
        return ResponseEntity.ok(analyticsService.getLogsByUserLocation(location));
    }

    @GetMapping("/logs/timeRange")
    @Operation(summary = "Get logs within specified time range")
    public ResponseEntity<List<SurveyAnalyticsLog>> getLogsByTimeRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(analyticsService.getLogsByTimeRange(start, end));
    }

    @GetMapping("/metrics")
    @Operation(summary = "Get all survey metrics")
    public ResponseEntity<List<SurveyMetrics>> getAllMetrics() {
        return ResponseEntity.ok(surveyAnalyticsService.getAllMetrics());
    }

    @GetMapping("/metrics/{id}")
    @Operation(summary = "Get survey metrics by ID")
    public ResponseEntity<SurveyMetrics> getMetricsById(@PathVariable Long id) {
        return surveyAnalyticsService.getMetricsById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/metrics")
    @Operation(summary = "Create new survey metrics")
    public ResponseEntity<SurveyMetrics> saveMetrics(@RequestBody SurveyMetrics metrics) {
        return ResponseEntity.status(HttpStatus.CREATED).body(surveyAnalyticsService.createMetrics(metrics));
    }

    @DeleteMapping("/metrics/{id}")
    @Operation(summary = "Delete survey metrics by ID")
    public ResponseEntity<Void> deleteMetrics(@PathVariable Long id) {
        surveyAnalyticsService.deleteMetrics(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/updateIndustryMetrics")
    @Operation(summary = "Update industry-specific metrics")
    public ResponseEntity<Void> updateIndustryMetrics(@RequestBody Map<String, String> request) {
        surveyAnalyticsService.updateIndustryMetrics(request.get("industry"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/trackPageView")
    @Operation(summary = "Track page view analytics")
    public ResponseEntity<Void> trackPageView(@RequestBody Map<String, String> request) {
        analyticsService.trackPageView(request.get("page"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/trackSurveyCompletion")
    @Operation(summary = "Track survey completion metrics")
    public ResponseEntity<Void> trackSurveyCompletion(@RequestBody Map<String, Object> request) {
        String userLocation = (String) request.get("userLocation");
        long timeTaken = Long.parseLong(request.get("timeTaken").toString());
        int responseLength = Integer.parseInt(request.get("responseLength").toString());

        analyticsService.trackSurveyCompletion(userLocation, timeTaken, responseLength);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/summary")
    @Operation(summary = "Get analytics summary report")
    public ResponseEntity<Map<String, Object>> getAnalyticsSummary() {
        return ResponseEntity.ok(analyticsService.getAnalyticsSummary());
    }

    @GetMapping("/report")
    @Operation(summary = "Generate PDF analytics report for specified domain")
    public ResponseEntity<byte[]> generateReport(@RequestParam String domain) {
        byte[] report = reportService.generateAnalyticsReport(domain);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                domain.replace(".", "-") + "-analytics-report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(report);
    }
}

