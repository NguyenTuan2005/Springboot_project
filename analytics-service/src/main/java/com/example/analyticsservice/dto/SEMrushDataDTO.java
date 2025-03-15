package com.example.analyticsservice.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
public class SEMrushDataDTO {
    private String domain;
    private Long traffic;
    private String keywords;
    private Double trafficCost;
}