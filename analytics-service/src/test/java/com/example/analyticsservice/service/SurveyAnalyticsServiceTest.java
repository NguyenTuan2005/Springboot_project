package com.example.analyticsservice.service;

import com.example.analyticsservice.model.SurveyMetrics;
import com.example.analyticsservice.repository.SurveyMetricsRepository;
import com.example.analyticsservice.service.impl.SurveyAnalyticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SurveyAnalyticsServiceTest {

    @Mock
    private SurveyMetricsRepository metricsRepository;

    @Captor
    private ArgumentCaptor<SurveyMetrics> metricsCaptor;

    private SurveyAnalyticsService surveyAnalyticsService;

    @BeforeEach
    void setUp() {
       surveyAnalyticsService = new SurveyAnalyticsServiceImpl(metricsRepository);
    }

    @Test
    void getAllMetrics_shouldReturnAllMetrics() {
        // Arrange
        SurveyMetrics metrics1 = new SurveyMetrics();
        metrics1.setId(1L);
        SurveyMetrics metrics2 = new SurveyMetrics();
        metrics2.setId(2L);
        List<SurveyMetrics> expectedMetrics = Arrays.asList(metrics1, metrics2);

        when(metricsRepository.findAll()).thenReturn(expectedMetrics);

        // Act
        List<SurveyMetrics> actualMetrics = surveyAnalyticsService.getAllMetrics();

        // Assert
        assertEquals(expectedMetrics, actualMetrics);
        verify(metricsRepository).findAll();
    }

    @Test
    void getMetricsById_shouldReturnMetricsWhenExists() {
        // Arrange
        Long metricsId = 1L;
        SurveyMetrics expectedMetrics = new SurveyMetrics();
        expectedMetrics.setId(metricsId);

        when(metricsRepository.findById(metricsId)).thenReturn(Optional.of(expectedMetrics));

        // Act
        Optional<SurveyMetrics> result = surveyAnalyticsService.getMetricsById(metricsId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedMetrics, result.get());
        verify(metricsRepository).findById(metricsId);
    }

    @Test
    void saveMetrics_shouldSaveAndReturn() {
        // Arrange
        SurveyMetrics metrics = new SurveyMetrics();
        metrics.setTotalResponses(100);

        when(metricsRepository.save(any(SurveyMetrics.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SurveyMetrics savedMetrics = surveyAnalyticsService.createMetrics(metrics);

        // Assert
        assertEquals(100, savedMetrics.getTotalResponses());
        verify(metricsRepository).save(metrics);
    }

    @Test
    void deleteMetrics_shouldDeleteById() {
        // Arrange
        Long metricsId = 1L;

        // Act
        surveyAnalyticsService.deleteMetrics(metricsId);

        // Assert
        verify(metricsRepository).deleteById(metricsId);
    }

    @Test
    void updateIndustryMetrics_shouldCreateNewMetricsIfNotExists() {
        // Arrange
        String industry = "Technology";
        when(metricsRepository.findAll()).thenReturn(List.of());
        when(metricsRepository.save(any(SurveyMetrics.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        surveyAnalyticsService.updateIndustryMetrics(industry);

        // Assert
        verify(metricsRepository).save(metricsCaptor.capture());
        SurveyMetrics savedMetrics = metricsCaptor.getValue();
        assertEquals(1, savedMetrics.getTotalResponses());
        assertEquals(1, savedMetrics.getPopularIndustries().get(industry));
    }

    @Test
    void updateIndustryMetrics_shouldUpdateExistingMetrics() {
        // Arrange
        String industry = "Technology";
        SurveyMetrics existingMetrics = new SurveyMetrics();
        existingMetrics.setTotalResponses(10);
        Map<String, Integer> industries = new HashMap<>();
        industries.put(industry, 5);
        industries.put("Healthcare", 3);
        existingMetrics.setPopularIndustries(industries);

        when(metricsRepository.findAll()).thenReturn(List.of(existingMetrics));
        when(metricsRepository.save(any(SurveyMetrics.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        surveyAnalyticsService.updateIndustryMetrics(industry);

        // Assert
        verify(metricsRepository).save(metricsCaptor.capture());
        SurveyMetrics savedMetrics = metricsCaptor.getValue();
        assertEquals(11, savedMetrics.getTotalResponses());
        assertEquals(6, savedMetrics.getPopularIndustries().get(industry));
        assertEquals(3, savedMetrics.getPopularIndustries().get("Healthcare"));
    }
}
