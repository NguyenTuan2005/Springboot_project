package com.example.gatewayservice.survey.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CustomerJourneyDTO {
    private Long id;
    private SurveyDTO survey;
    private JourneyStage stage;
    private String touchpoint;
    private LocalDateTime timestamp;

    public enum JourneyStage {
        AWARENESS, CONSIDERATION, DECISION
    }
}