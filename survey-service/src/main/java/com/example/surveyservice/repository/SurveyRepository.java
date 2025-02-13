package com.example.surveyservice.repository;

import com.example.surveyservice.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
}

