package com.example.analyticsservice.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class SEMrushDataDTO {
    private String domain;
    private Long traffic;
    private String keywords;
    private Double trafficCost;
    }