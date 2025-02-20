package com.example.analyticsservice.controller;

import com.example.analyticsservice.model.CompetitorTrend;
import com.example.analyticsservice.service.CompetitorTrendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/competitors")
public class CompetitorTrendController {

    private final CompetitorTrendService competitorTrendService;

    public CompetitorTrendController(CompetitorTrendService competitorTrendService) {
        this.competitorTrendService = competitorTrendService;
    }

    @GetMapping("/trends")
    public ResponseEntity<List<CompetitorTrend>> getTrends(@RequestParam String keyword) {
        List<CompetitorTrend> trends = competitorTrendService.getTrendsForKeyword(keyword);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/fetch-trends")
    public ResponseEntity<String> manualFetchTrends() {
        competitorTrendService.fetchAndStoreTrends();
        return ResponseEntity.ok("Trends fetched and stored.");
    }
}