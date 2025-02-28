package com.example.surveyservice.controller;

import com.example.surveyservice.model.CROTest;
import com.example.surveyservice.model.CROResult;
import com.example.surveyservice.service.CROService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cro")
public class CROController {

    private final CROService croService;

    public CROController(CROService croService) {
        this.croService = croService;
    }

    @PostMapping("/results")
    public ResponseEntity<CROResult> creatResult(@RequestBody CROResult result) {
        return ResponseEntity.ok(croService.creatResult(result));
    }

    @PostMapping("/tests")
    public ResponseEntity<CROTest> createTest(@RequestBody CROTest test) {
        return ResponseEntity.ok(croService.createTest(test));
    }

    @GetMapping("/results")
    public ResponseEntity<List<CROResult>> getResults(@RequestParam String testId) {
        return ResponseEntity.ok(croService.getTestResults(testId));
    }
}