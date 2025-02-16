package com.example.analyticsservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "survey_metrics")
public class SurveyMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalResponses;
    private double averageCompanySize;

    @ElementCollection
    @CollectionTable(name = "popular_industries", joinColumns = @JoinColumn(name = "survey_metrics_id"))
    @MapKeyColumn(name = "industry")
    @Column(name = "count")
    private Map<String, Integer> popularIndustries;

    // Getters and setters
}

