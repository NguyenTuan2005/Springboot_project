package com.example.surveyservice.controller;

import com.example.surveyservice.model.CROTest;
import com.example.surveyservice.model.CROResult;
import com.example.surveyservice.service.CROService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cro")
@Tag(name = "Conversion Rate Optimization", description = "CRO management APIs")
public class CROController {

    private final CROService croService;

    public CROController(CROService croService) {
        this.croService = croService;
    }

    @Operation(summary = "Create new CRO result")
    @PostMapping("/results")
    public ResponseEntity<CROResult> createCROResult(@RequestBody CROResult result) {
        return ResponseEntity.ok(croService.createResult(result));
    }

    @Operation(summary = "Get all CRO results for a specific test")
    @GetMapping("/{testId}/results/")
    public ResponseEntity<List<CROResult>> getCROResults(@PathVariable Long testId) {
        Optional<List<CROResult>> resultsOptional = croService.getTestResults(testId);
        if (resultsOptional.isEmpty() || resultsOptional.get().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(resultsOptional.get());
    }

    @Operation(summary = "Get all CRO results")
    @GetMapping("/results")
    public ResponseEntity<List<CROResult>> getAllCROResults() {
        List<CROResult> results = croService.getAllResults();
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Get CRO result by ID")
    @GetMapping("/results/{id}")
    public ResponseEntity<CROResult> getCROResultById(@PathVariable Long id) {
        return croService.getResultById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update CRO result")
    @PutMapping("/results/{id}")
    public ResponseEntity<CROResult> updateCROResult(@PathVariable Long id, @RequestBody CROResult result) {
        return croService.updateResult(id, result)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete CRO result")
    @DeleteMapping("/results/{id}")
    public ResponseEntity<Void> deleteCROResult(@PathVariable Long id) {
        croService.deleteResult(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create new CRO test")
    @PostMapping("/tests")
    public ResponseEntity<CROTest> createCROTest(@RequestBody CROTest test) {
        CROTest createdTest = croService.createTest(test);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTest);
    }

    @Operation(summary = "Get CRO test by ID")
    @GetMapping("/tests/{id}")
    public ResponseEntity<CROTest> getCROTest(@PathVariable Long id) {
        return croService.getTestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all CRO tests")
    @GetMapping("/tests")
    public ResponseEntity<List<CROTest>> getAllCROTests() {
        return ResponseEntity.ok(croService.getAllTests());
    }

    @Operation(summary = "Update CRO test")
    @PutMapping("/tests/{id}")
    public ResponseEntity<CROTest> updateCROTest(@PathVariable Long id, @RequestBody CROTest test) {
        return croService.updateTest(id, test)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete CRO test")
    @DeleteMapping("/tests/{id}")
    public ResponseEntity<Void> deleteCROTest(@PathVariable Long id) {
        croService.deleteTest(id);
        return ResponseEntity.noContent().build();
    }
}