package com.example.surveyservice.service;

import java.util.*;

import com.example.surveyservice.model.CROResult;
import com.example.surveyservice.model.CROTest;

public interface CROService {

    CROResult createResult(CROResult result);

    List<CROResult> getAllResults();

    Optional<CROResult> getResultById(long id);

    Optional<CROResult> updateResult(Long id, CROResult updateResult);

    boolean deleteResult(Long id);

    CROTest createTest(CROTest test);

    Optional<CROTest> getTestById(Long id);

    List<CROTest> getAllTests();

    Optional<CROTest> updateTest(Long id, CROTest updatedTest);

    boolean deleteTest(Long id);

    boolean recordImpression(Long testId, String variant);

    boolean recordConversion(Long testId, String variant);

    Optional<List<CROResult>> getTestResults(Long testId);

    Optional<Map<String, Double>> getConversionRates(Long testId);

    Optional<String> getWinningVariant(Long testId);
}