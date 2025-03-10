package com.example.surveyservice.service.impl;

import com.example.surveyservice.model.CustomerJourney;
import com.example.surveyservice.repository.CustomerJourneyRepository;
import com.example.surveyservice.service.CustomerJourneyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerJourneyServiceImpl implements CustomerJourneyService {
    private final CustomerJourneyRepository customerJourneyRepository;

    public CustomerJourneyServiceImpl(CustomerJourneyRepository customerJourneyRepository) {
        this.customerJourneyRepository = customerJourneyRepository;
    }

    @Override
    public List<CustomerJourney> getJourneyBySurveyId(Long surveyId) {
        return customerJourneyRepository.findBySurveyIdOrderByTimestampAsc(surveyId);
    }

    @Override
    public CustomerJourney addJourneyTouchpoint(CustomerJourney customerJourney) {
        return customerJourneyRepository.save(customerJourney);
    }
}
