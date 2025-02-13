package com.example.surveyservice.service;

import com.example.surveyservice.config.RabbitMQConfig;
import com.example.surveyservice.model.SurveyResponse;
import com.example.surveyservice.repository.SurveyResponseRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SurveyService {

    private final SurveyResponseRepository surveyResponseRepository;

    private final RabbitTemplate rabbitTemplate;

    public SurveyService(SurveyResponseRepository surveyResponseRepository, RabbitTemplate rabbitTemplate) {
        this.surveyResponseRepository = surveyResponseRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public SurveyResponse saveSurveyResponse(SurveyResponse surveyResponse) {
        SurveyResponse savedResponse = surveyResponseRepository.save(surveyResponse);

        // Send message to RabbitMQ
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, savedResponse);

        return savedResponse;
    }

    public List<SurveyResponse> getAllSurveyResponses() {
        return surveyResponseRepository.findAll();
    }
}

