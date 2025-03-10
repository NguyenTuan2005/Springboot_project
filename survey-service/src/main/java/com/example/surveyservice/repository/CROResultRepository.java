package com.example.surveyservice.repository;

import com.example.surveyservice.model.CROResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CROResultRepository extends JpaRepository<CROResult, Long> {
    List<CROResult> findByTestId(Long testId);
    Optional<CROResult> findByTestIdAndVariant(Long testId, String variant);
}
