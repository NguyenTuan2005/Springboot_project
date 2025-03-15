package com.example.analyticsservice.controller;

import com.example.analyticsservice.model.CompetitorTrend;
import com.example.analyticsservice.service.CompetitorTrendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/competitors")
@Tag(name = "Competitor", description = "Competitor management APIs")
public class CompetitorTrendController {

    private final CompetitorTrendService competitorTrendService;

    public CompetitorTrendController(CompetitorTrendService competitorTrendService) {
        this.competitorTrendService = competitorTrendService;
    }

    @GetMapping("/{keyword}")
    @Operation(summary = "Get competitor trends by keyword")
    public ResponseEntity<List<CompetitorTrend>> getTrendsByKeyword(@PathVariable String keyword) {
        List<CompetitorTrend> trends = competitorTrendService.getTrendsForKeyword(keyword);
        if (trends.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(trends);
    }

    @PostMapping("/fetch")
    @Operation(summary = "Trigger trend fetch process")
    public ResponseEntity<String> triggerFetchTrends() {
        try {
            competitorTrendService.fetchAndStoreTrends();
            return ResponseEntity.ok("Trend fetch process started successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error starting trend fetch: " + e.getMessage());
        }
    }
}