package com.example.surveyservice.repository;

import com.example.surveyservice.model.CustomerJourney;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CustomerJourneyRepository extends JpaRepository<CustomerJourney, Long> {
    List<CustomerJourney> findBySurveyIdOrderByTimestampAsc(Long surveyId);
}

