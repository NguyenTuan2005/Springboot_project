package com.example.surveyservice.service;

import com.example.surveyservice.model.CustomerJourney;

import java.util.List;

public interface CustomerJourneyService {

    List<CustomerJourney> getJourneyBySurveyId(Long surveyId);

    CustomerJourney addJourneyTouchpoint(CustomerJourney customerJourney);
}

