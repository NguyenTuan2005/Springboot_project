package com.example.analyticsservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "survey_analytics_logs")
public class SurveyAnalyticsLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long timeTaken;
    private String userLocation;
    private int responseLength;
    private LocalDateTime timestamp;

}

