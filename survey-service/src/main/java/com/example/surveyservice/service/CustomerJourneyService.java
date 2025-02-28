package com.example.surveyservice.service;

import com.example.surveyservice.model.CustomerJourney;
import com.example.surveyservice.repository.CustomerJourneyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerJourneyService {

    private final CustomerJourneyRepository customerJourneyRepository;

    public CustomerJourneyService(CustomerJourneyRepository customerJourneyRepository) {
        this.customerJourneyRepository = customerJourneyRepository;
    }

    public List<CustomerJourney> getJourneyBySurveyId(Long surveyId) {
        return customerJourneyRepository.findBySurveyIdOrderByTimestampAsc(surveyId);
    }

    public CustomerJourney addJourneyTouchpoint(CustomerJourney customerJourney) {
        return customerJourneyRepository.save(customerJourney);
    }
}

