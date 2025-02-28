package com.example.surveyservice.repository;

import com.example.surveyservice.model.CROResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CROResultRepository extends JpaRepository<CROResult, Long> {
    List<CROResult> findCROResultByTestId(long id);
}
