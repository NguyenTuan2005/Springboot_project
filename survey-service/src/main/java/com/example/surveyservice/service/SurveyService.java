package com.example.surveyservice.service;

import com.example.surveyservice.model.Survey;
import com.example.surveyservice.model.SurveyResponse;
import com.example.surveyservice.request.CreateSurveyResponseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SurveyService {

    SurveyResponse createSurveyResponse(Long surveyId, CreateSurveyResponseRequest createSurveyResponseRequest);

    List<SurveyResponse> getSurveyResponses(Long surveyId);

    Page<SurveyResponse> getSurveyResponsesPaginated(Long id, Pageable pageable);

    List<SurveyResponse> getAllSurveyResponses();

    Survey createSurvey(Survey survey);

    List<Survey> getAllSurveys();

    Optional<Survey> getSurveyById(Long id);

    Optional<Survey> updateSurvey(Long id, Survey updatedSurvey);

    boolean deleteSurvey(Long id);

    List<Survey> getSurveysByTargetIndustry(String targetIndustry);
}

