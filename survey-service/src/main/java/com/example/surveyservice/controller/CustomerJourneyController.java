package com.example.surveyservice.controller;

import com.example.surveyservice.model.CustomerJourney;
import com.example.surveyservice.service.CustomerJourneyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/journey")
@Tag(name = "Journey", description = "Journey management APIs")
@RequiredArgsConstructor
public class CustomerJourneyController {

    private final CustomerJourneyService customerJourneyService;

    // public CustomerJourneyController(CustomerJourneyService customerJourneyService) {
    //     this.customerJourneyService = customerJourneyService;
    // }

    @GetMapping("/{surveyId}")
    @Operation(summary = "Get journey by survey ID")
    public ResponseEntity<List<CustomerJourney>> getJourney(@PathVariable Long surveyId) {
        List<CustomerJourney> journey = customerJourneyService.getJourneyBySurveyId(surveyId);
        return ResponseEntity.ok(journey);
    }

    @PostMapping
    @Operation(summary = "Add journey touchpoint")
    public ResponseEntity<CustomerJourney> addJourneyTouchpoint(@RequestBody CustomerJourney customerJourney) {
        CustomerJourney savedJourney = customerJourneyService.addJourneyTouchpoint(customerJourney);
        return ResponseEntity.ok(savedJourney);
    }
}

