package com.example.surveyservice.repository;

import com.example.surveyservice.model.SurveyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
    List<SurveyResponse> findBySurveyId(Long l);
    Page<SurveyResponse> findBySurveyId(Long id, Pageable pageable);
}

