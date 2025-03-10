package com.example.surveyservice.repository;

import com.example.surveyservice.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    List<Survey> findByTargetIndustry(String s);
}

