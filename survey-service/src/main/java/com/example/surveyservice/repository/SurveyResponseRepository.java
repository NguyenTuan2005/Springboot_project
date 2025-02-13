package com.example.surveyservice.repository;

import com.example.surveyservice.model.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
}

