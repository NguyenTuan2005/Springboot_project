package com.example.surveyservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "customer_journey")
public class CustomerJourney {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @Enumerated(EnumType.STRING)
    private JourneyStage stage;

    private String touchpoint;
    private LocalDateTime timestamp;

    public enum JourneyStage {
        AWARENESS, CONSIDERATION, DECISION
    }
}

