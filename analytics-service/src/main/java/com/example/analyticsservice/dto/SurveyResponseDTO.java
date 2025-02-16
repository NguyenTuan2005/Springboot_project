package com.example.analyticsservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class SurveyResponseDTO {
    private Long id;
    private SurveyDTO survey;
    private String companySize;
    private Map<String, Object> answers;
}
