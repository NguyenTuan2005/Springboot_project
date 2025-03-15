package com.example.analyticsservice.service;

import com.example.analyticsservice.model.SurveyMetrics;
import com.example.analyticsservice.service.impl.AnalyticsServiceImpl;
import com.example.shared.model.SurveyAnalyticsLog;
import com.example.shared.repository.SurveyAnalyticsLogRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnalyticsServiceTest {
    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private SurveyAnalyticsLogRepository logRepository;

    @Mock
    private SurveyAnalyticsService surveyAnalyticsService;

    @Mock
    private Counter counter;

    @Mock
    private Timer timer;

    @Captor
    private ArgumentCaptor<SurveyAnalyticsLog> logCaptor;

    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        analyticsService = new AnalyticsServiceImpl(meterRegistry, logRepository, surveyAnalyticsService);
    }

    @Test
    void getAllLogs_shouldReturnAllLogs() {
        // Arrange
        SurveyAnalyticsLog log1 = new SurveyAnalyticsLog();
        log1.setId(1L);
        SurveyAnalyticsLog log2 = new SurveyAnalyticsLog();
        log2.setId(2L);
        List<SurveyAnalyticsLog> expectedLogs = Arrays.asList(log1, log2);

        when(logRepository.findAll()).thenReturn(expectedLogs);

        // Act
        List<SurveyAnalyticsLog> actualLogs = analyticsService.getAllLogs();

        // Assert
        assertEquals(expectedLogs, actualLogs);
        verify(logRepository).findAll();
    }

    @Test
    void getLogById_shouldReturnLogWhenExists() {
        // Arrange
        Long logId = 1L;
        SurveyAnalyticsLog expectedLog = new SurveyAnalyticsLog();
        expectedLog.setId(logId);

        when(logRepository.findById(logId)).thenReturn(Optional.of(expectedLog));

        // Act
        Optional<SurveyAnalyticsLog> result = analyticsService.getLogById(logId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedLog, result.get());
        verify(logRepository).findById(logId);
    }

    @Test
    void getLogById_shouldReturnEmptyWhenNotExists() {
        // Arrange
        Long logId = 1L;
        when(logRepository.findById(logId)).thenReturn(Optional.empty());

        // Act
        Optional<SurveyAnalyticsLog> result = analyticsService.getLogById(logId);

        // Assert
        assertFalse(result.isPresent());
        verify(logRepository).findById(logId);
    }

    @Test
    void saveLog_shouldSetTimestampAndSave() {
        // Arrange
        SurveyAnalyticsLog log = new SurveyAnalyticsLog();
        log.setUserLocation("New York");
        log.setTimeTaken(120);
        log.setResponseLength(500);

        when(logRepository.save(any(SurveyAnalyticsLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SurveyAnalyticsLog savedLog = analyticsService.createLog(log);

        // Assert
        assertNotNull(savedLog.getTimestamp());
        assertEquals("New York", savedLog.getUserLocation());
        assertEquals(120, savedLog.getTimeTaken());
        assertEquals(500, savedLog.getResponseLength());
        verify(logRepository).save(log);
    }

    @Test
    void saveLog_shouldNotOverrideExistingTimestamp() {
        // Arrange
        LocalDateTime existingTimestamp = LocalDateTime.now().minusDays(1);
        SurveyAnalyticsLog log = new SurveyAnalyticsLog();
        log.setUserLocation("New York");
        log.setTimestamp(existingTimestamp);

        when(logRepository.save(any(SurveyAnalyticsLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SurveyAnalyticsLog savedLog = analyticsService.createLog(log);

        // Assert
        assertEquals(existingTimestamp, savedLog.getTimestamp());
        verify(logRepository).save(log);
    }

    @Test
    void deleteLog_shouldDeleteById() {
        // Arrange
        Long logId = 1L;

        // Act
        analyticsService.deleteLog(logId);

        // Assert
        verify(logRepository).deleteById(logId);
    }

    @Test
    void getLogsByUserLocation_shouldReturnFilteredLogs() {
        // Arrange
        String location = "New York";
        SurveyAnalyticsLog log1 = new SurveyAnalyticsLog();
        log1.setUserLocation(location);
        List<SurveyAnalyticsLog> expectedLogs = List.of(log1);

        when(logRepository.findByUserLocation(location)).thenReturn(expectedLogs);

        // Act
        List<SurveyAnalyticsLog> actualLogs = analyticsService.getLogsByUserLocation(location);

        // Assert
        assertEquals(expectedLogs, actualLogs);
        verify(logRepository).findByUserLocation(location);
    }

    @Test
    void getLogsByTimeRange_shouldReturnFilteredLogs() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        SurveyAnalyticsLog log1 = new SurveyAnalyticsLog();
        List<SurveyAnalyticsLog> expectedLogs = List.of(log1);

        when(logRepository.findByTimestampBetween(start, end)).thenReturn(expectedLogs);

        // Act
        List<SurveyAnalyticsLog> actualLogs = analyticsService.getLogsByTimeRange(start, end);

        // Assert
        assertEquals(expectedLogs, actualLogs);
        verify(logRepository).findByTimestampBetween(start, end);
    }

    @Test
    void trackPageView_shouldIncrementCounter() {
        // Arrange
        String page = "survey";
        when(meterRegistry.counter("page.views", "page", page)).thenReturn(counter);

        // Act
        analyticsService.trackPageView(page);

        // Assert
        verify(meterRegistry).counter("page.views", "page", page);
        verify(counter).increment();
    }

    @Test
    void trackSurveyCompletion_shouldUpdateMetricsAndSaveLog() {
        // Arrange
        String userLocation = "New York";
        long timeTaken = 150;
        int responseLength = 750;

        when(meterRegistry.counter("survey.completions")).thenReturn(counter);
        when(meterRegistry.timer("survey.completion.time")).thenReturn(timer);
        when(logRepository.save(any(SurveyAnalyticsLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        analyticsService.trackSurveyCompletion(userLocation, timeTaken, responseLength);

        // Assert
        verify(meterRegistry).counter("survey.completions");
        verify(counter).increment();
        verify(meterRegistry).timer("survey.completion.time");
        verify(timer).record(timeTaken, TimeUnit.MILLISECONDS);
        verify(meterRegistry).gauge(eq("survey.response.length"), eq(responseLength));

        verify(logRepository).save(logCaptor.capture());
        SurveyAnalyticsLog savedLog = logCaptor.getValue();
        assertEquals(userLocation, savedLog.getUserLocation());
        assertEquals(timeTaken, savedLog.getTimeTaken());
        assertEquals(responseLength, savedLog.getResponseLength());
        assertNotNull(savedLog.getTimestamp());
    }

    @Test
    void getAnalyticsSummary_shouldReturnCompleteSummary() {
        // Arrange
        long totalLogs = 25;

        SurveyAnalyticsLog log1 = new SurveyAnalyticsLog();
        log1.setTimeTaken(100);
        SurveyAnalyticsLog log2 = new SurveyAnalyticsLog();
        log2.setTimeTaken(200);
        List<SurveyAnalyticsLog> logs = Arrays.asList(log1, log2);

        SurveyMetrics metrics = new SurveyMetrics();
        metrics.setTotalResponses(15);
        metrics.setAverageCompanySize(250.5);
        Map<String, Integer> industries = new HashMap<>();
        industries.put("Technology", 6);
        industries.put("Healthcare", 4);
        metrics.setPopularIndustries(industries);

        when(logRepository.count()).thenReturn(totalLogs);
        when(logRepository.findAll()).thenReturn(logs);
        when(surveyAnalyticsService.getAllMetrics()).thenReturn(List.of(metrics));

        // Act
        Map<String, Object> summary = analyticsService.getAnalyticsSummary();

        // Assert
        assertEquals(totalLogs, summary.get("totalLogs"));
        assertEquals(150.0, summary.get("averageTimeTaken"));
        assertEquals(15, summary.get("totalResponses"));
        assertEquals(250.5, summary.get("averageCompanySize"));

        @SuppressWarnings("unchecked")
        Map<String, Integer> popularIndustries = (Map<String, Integer>) summary.get("popularIndustries");
        assertEquals(6, popularIndustries.get("Technology"));
        assertEquals(4, popularIndustries.get("Healthcare"));
    }

    @Test
    void getAnalyticsSummary_shouldHandleNoMetrics() {
        // Arrange
        long totalLogs = 25;

        SurveyAnalyticsLog log1 = new SurveyAnalyticsLog();
        log1.setTimeTaken(100);
        SurveyAnalyticsLog log2 = new SurveyAnalyticsLog();
        log2.setTimeTaken(200);
        List<SurveyAnalyticsLog> logs = Arrays.asList(log1, log2);

        when(logRepository.count()).thenReturn(totalLogs);
        when(logRepository.findAll()).thenReturn(logs);
        when(surveyAnalyticsService.getAllMetrics()).thenReturn(List.of());

        // Act
        Map<String, Object> summary = analyticsService.getAnalyticsSummary();

        // Assert
        assertEquals(totalLogs, summary.get("totalLogs"));
        assertEquals(150.0, summary.get("averageTimeTaken"));
        assertFalse(summary.containsKey("totalResponses"));
        assertFalse(summary.containsKey("averageCompanySize"));
        assertFalse(summary.containsKey("popularIndustries"));
    }
}
