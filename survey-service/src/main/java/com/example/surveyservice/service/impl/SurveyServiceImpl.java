package com.example.surveyservice.service.impl;

import com.example.surveyservice.config.RabbitMQConfig;
import com.example.surveyservice.model.Survey;
import com.example.surveyservice.model.SurveyResponse;
import com.example.surveyservice.repository.SurveyRepository;
import com.example.surveyservice.repository.SurveyResponseRepository;
import com.example.surveyservice.request.CreateSurveyResponseRequest;
import com.example.surveyservice.service.SurveyService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SurveyServiceImpl implements SurveyService {
    private final SurveyResponseRepository surveyResponseRepository;

    private final SurveyRepository surveyRepository;

    private final RabbitTemplate rabbitTemplate;

    public SurveyServiceImpl(SurveyResponseRepository surveyResponseRepository, SurveyRepository surveyRepository, RabbitTemplate rabbitTemplate) {
        this.surveyResponseRepository = surveyResponseRepository;
        this.surveyRepository = surveyRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public SurveyResponse createSurveyResponse(Long surveyId, CreateSurveyResponseRequest createSurveyResponseRequest) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey with id " + surveyId + " not found"));
        SurveyResponse surveyResponse = SurveyResponse.builder()
                .survey(survey)
                .companySize(createSurveyResponseRequest.getCompanySize())
                .answers(createSurveyResponseRequest.getAnswers())
                .build();

        SurveyResponse savedResponse = surveyResponseRepository.save(surveyResponse);

        // Send message to RabbitMQ
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, savedResponse);

        return savedResponse;
    }

    @Override
    public List<SurveyResponse> getSurveyResponses(Long surveyId) {
        return surveyResponseRepository.findBySurveyId(surveyId);
    }

    @Override
    public Page<SurveyResponse> getSurveyResponsesPaginated(Long id, Pageable pageable) {
        return surveyResponseRepository.findBySurveyId(id, pageable);
    }

    @Override
    public List<SurveyResponse> getAllSurveyResponses() {
        return surveyResponseRepository.findAll();
    }

    @Override
    public Survey createSurvey(Survey survey) {
        return surveyRepository.save(survey);
    }

    @Override
    public List<Survey> getAllSurveys() {
        return surveyRepository.findAll();
    }

    @Override
    public Optional<Survey> getSurveyById(Long id) {
        return surveyRepository.findById(id);
    }

    @Override
    public Optional<Survey> updateSurvey(Long id, Survey updatedSurvey) {
        return surveyRepository.findById(id)
                .map(survey -> {
                    survey.setTitle(updatedSurvey.getTitle());
                    survey.setTargetIndustry(updatedSurvey.getTargetIndustry());
                    return surveyRepository.save(survey);
                });
    }

    @Override
    public boolean deleteSurvey(Long id) {
        return surveyRepository.findById(id)
                .map(survey -> {
                    surveyRepository.delete(survey);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public List<Survey> getSurveysByTargetIndustry(String targetIndustry){
        return surveyRepository.findByTargetIndustry(targetIndustry);
    }
}
