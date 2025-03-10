package com.example.surveyservice.service.impl;

import com.example.surveyservice.model.CROResult;
import com.example.surveyservice.model.CROTest;
import com.example.surveyservice.repository.CROResultRepository;
import com.example.surveyservice.repository.CROTestRepository;
import com.example.surveyservice.service.CROService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CROServiceImpl implements CROService {
    private final CROTestRepository croTestRepository;
    private final CROResultRepository croResultRepository;

    @Autowired
    public CROServiceImpl(CROTestRepository croTestRepository, CROResultRepository croResultRepository) {
        this.croTestRepository = croTestRepository;
        this.croResultRepository = croResultRepository;
    }

    @Override
    public CROResult createResult(CROResult result) { return croResultRepository.save(result); }

    @Override
    public List<CROResult> getAllResults() {
        return croResultRepository.findAll();
    }

    @Override
    public Optional<CROResult> getResultById(long id) {
        return croResultRepository.findById(id);
    }

    @Override
    public Optional<CROResult> updateResult(Long id, CROResult updateResult) {
        return croResultRepository.findById(id)
                .map(existingResult -> {
                    existingResult.setVariant(updateResult.getVariant());
                    existingResult.setTest(updateResult.getTest());
                    existingResult.setImpressions(updateResult.getImpressions());
                    existingResult.setConversions(updateResult.getConversions());
                    return croResultRepository.save(existingResult);
                });
    }

    @Override
    public boolean deleteResult(Long id) {
        return croResultRepository.findById(id)
                .map(result -> {
                    croResultRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public CROTest createTest(CROTest test) {
        return croTestRepository.save(test);
    }

    @Override
    public Optional<CROTest> getTestById(Long id) {
        return croTestRepository.findById(id);
    }

    @Override
    public List<CROTest> getAllTests() {
        return croTestRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<CROTest> updateTest(Long id, CROTest updatedTest) {
        return croTestRepository.findById(id)
                .map(existingTest -> {
                    existingTest.setName(updatedTest.getName());
                    existingTest.setTargetUrl(updatedTest.getTargetUrl());
                    existingTest.setVariants(updatedTest.getVariants());
                    return croTestRepository.save(existingTest);
                });
    }

    @Override
    @Transactional
    public boolean deleteTest(Long id) {
        return croTestRepository.findById(id)
                .map(test -> {
                    croTestRepository.delete(test);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public boolean recordImpression(Long testId, String variant) {
        return croTestRepository.findById(testId)
                .map(test -> {
                    CROResult result = croResultRepository.findByTestIdAndVariant(testId, variant)
                            .orElseGet(() -> {
                                CROResult newResult = new CROResult();
                                newResult.setTest(test);
                                newResult.setVariant(variant);
                                newResult.setImpressions(0);
                                newResult.setConversions(0);
                                return newResult;
                            });

                    result.setImpressions(result.getImpressions() + 1);
                    croResultRepository.save(result);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public boolean recordConversion(Long testId, String variant) {
        return croTestRepository.findById(testId)
                .map(test -> {
                    return croResultRepository.findByTestIdAndVariant(testId, variant)
                            .map(result -> {
                                result.setConversions(result.getConversions() + 1);
                                croResultRepository.save(result);
                                return true;
                            })
                            .orElse(false);
                })
                .orElse(false);
    }

    @Override
    public Optional<List<CROResult>> getTestResults(Long testId) {
        return croTestRepository.findById(testId)
                .map(test -> croResultRepository.findByTestId(testId));
    }

    @Override
    public Optional<Map<String, Double>> getConversionRates(Long testId) {
        return getTestResults(testId)
                .map(results -> results.stream()
                        .collect(Collectors.toMap(
                                CROResult::getVariant,
                                result -> result.getImpressions() > 0
                                        ? (double) result.getConversions() / result.getImpressions()
                                        : 0.0
                        ))
                );
    }

    @Override
    public Optional<String> getWinningVariant(Long testId) {
        return getConversionRates(testId)
                .flatMap(rates -> rates.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey));
    }
}
