package com.example.surveyservice.service;

import com.example.surveyservice.model.CROTest;
import com.example.surveyservice.model.CROResult;
import com.example.surveyservice.repository.CROResultRepository;
import com.example.surveyservice.repository.CROTestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CROService {

    private final CROTestRepository croRepository;

    private final CROResultRepository croResultRepository;

    public CROService(CROTestRepository croRepository, CROResultRepository croResultRepository) {
        this.croRepository = croRepository;
        this.croResultRepository = croResultRepository;
    }

    public CROResult creatResult(CROResult result) { return croResultRepository.save(result); }

    public CROTest createTest(CROTest test) {
        return croRepository.save(test);
    }

    public List<CROResult> getTestResults(String testId) {
        return croResultRepository.findCROResultByTestId(Long.parseLong(testId));
    }
}