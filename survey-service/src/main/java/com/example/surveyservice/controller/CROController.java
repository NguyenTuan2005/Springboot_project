package com.example.surveyservice.controller;

import com.example.surveyservice.model.CROTest;
import com.example.surveyservice.model.CROResult;
import com.example.surveyservice.service.CROService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cro")
public class CROController {

    private final CROService croService;

    public CROController(CROService croService) {
        this.croService = croService;
    }

    @Operation(summary = "Create new CRO result")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Result created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/results")
    public ResponseEntity<CROResult> createCROResult(@RequestBody CROResult result) {
        return ResponseEntity.ok(croService.createResult(result));
    }

    @Operation(summary = "Get all CRO results for a specific test")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Results retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Test not found")
    })
    @GetMapping("/results")
    public ResponseEntity<List<CROResult>> getCROResults(@RequestParam String testId) {
        List<CROResult> results = croService.getTestResults(Long.valueOf(testId)).get();
        if (results == null || results.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Get all CRO results")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Results retrieved successfully")
    })
    @GetMapping("/results/all")
    public ResponseEntity<List<CROResult>> getAllCROResults() {
        List<CROResult> results = croService.getAllResults();
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Get CRO result by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Result retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Result not found")
    })
    @GetMapping("/results/{id}")
    public ResponseEntity<CROResult> getCROResultById(@PathVariable Long id) {
        return croService.getResultById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update CRO result")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Result updated successfully"),
        @ApiResponse(responseCode = "404", description = "Result not found")
    })
    @PutMapping("/results/{id}")
    public ResponseEntity<CROResult> updateCROResult(@PathVariable Long id, @RequestBody CROResult result) {
        return croService.updateResult(id, result)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete CRO result")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Result deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Result not found")
    })
    @DeleteMapping("/results/{id}")
    public ResponseEntity<Void> deleteCROResult(@PathVariable Long id) {
        croService.deleteResult(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create new CRO test")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Test created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/tests")
    public ResponseEntity<CROTest> createCROTest(@RequestBody CROTest test) {
        CROTest createdTest = croService.createTest(test);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTest);
    }

    @Operation(summary = "Get CRO test by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Test found"),
        @ApiResponse(responseCode = "404", description = "Test not found")
    })
    @GetMapping("/tests/{id}")
    public ResponseEntity<CROTest> getCROTest(@PathVariable Long id) {
        return croService.getTestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all CRO tests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of tests")
    })
    @GetMapping("/tests")
    public ResponseEntity<List<CROTest>> getAllCROTests() {
        return ResponseEntity.ok(croService.getAllTests());
    }

    @Operation(summary = "Update CRO test")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Test updated successfully"),
        @ApiResponse(responseCode = "404", description = "Test not found")
    })
    @PutMapping("/tests/{id}")
    public ResponseEntity<CROTest> updateCROTest(@PathVariable Long id, @RequestBody CROTest test) {
        return croService.updateTest(id, test)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete CRO test")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Test deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Test not found")
    })
    @DeleteMapping("/tests/{id}")
    public ResponseEntity<Void> deleteCROTest(@PathVariable Long id) {
        croService.deleteTest(id);
        return ResponseEntity.noContent().build();
    }
}