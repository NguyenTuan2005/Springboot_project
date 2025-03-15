package com.example.analyticsservice.controller;

import com.example.analyticsservice.model.SurveyMetrics;
import com.example.analyticsservice.service.AnalyticsService;
import com.example.analyticsservice.service.SurveyAnalyticsService;
import com.example.shared.model.SurveyAnalyticsLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyticsController.class)
@WithMockUser(username = "test")
public class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @MockBean
    private SurveyAnalyticsService surveyAnalyticsService;

    private SurveyAnalyticsLog sampleLog;
    private SurveyMetrics sampleMetrics;
    private Map<String, Object> sampleSummary;

    @BeforeEach
    void setUp() {
        // Initialize sample log
        sampleLog = new SurveyAnalyticsLog();
        sampleLog.setId(1L);
        sampleLog.setUserLocation("New York");
        sampleLog.setTimeTaken(5000L);
        sampleLog.setResponseLength(200);
        sampleLog.setTimestamp(LocalDateTime.now());

        // Initialize sample metrics
        sampleMetrics = new SurveyMetrics();
        sampleMetrics.setId(1L);
        sampleMetrics.setTotalResponses(100);
        sampleMetrics.setAverageCompanySize(500);
        Map<String, Integer> industries = new HashMap<>();
        industries.put("Technology", 40);
        industries.put("Healthcare", 25);
        industries.put("Finance", 35);
        sampleMetrics.setPopularIndustries(industries);

        // Initialize sample summary
        sampleSummary = new HashMap<>();
        sampleSummary.put("totalLogs", 150L);
        sampleSummary.put("averageTimeTaken", 4500.0);
        sampleSummary.put("totalResponses", 100);
        sampleSummary.put("popularIndustries", industries);
    }

    @Test
    void getAllLogs_ShouldReturnAllLogs() throws Exception {
        when(analyticsService.getAllLogs()).thenReturn(Arrays.asList(sampleLog));

        mockMvc.perform(get("/api/v1/analytics/logs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userLocation").value("New York"))
                .andExpect(jsonPath("$[0].timeTaken").value(5000));

        verify(analyticsService, times(1)).getAllLogs();
    }

    @Test
    void getLogById_WhenLogExists_ShouldReturnLog() throws Exception {
        when(analyticsService.getLogById(1L)).thenReturn(Optional.of(sampleLog));

        mockMvc.perform(get("/api/v1/analytics/logs/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userLocation").value("New York"));

        verify(analyticsService, times(1)).getLogById(1L);
    }

    @Test
    void getLogById_WhenLogDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(analyticsService.getLogById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/analytics/logs/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(analyticsService, times(1)).getLogById(999L);
    }

    @Test
    void saveLog_ShouldReturnSavedLog() throws Exception {
        when(analyticsService.createLog(any(SurveyAnalyticsLog.class))).thenReturn(sampleLog);

        mockMvc.perform(post("/api/v1/analytics/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userLocation\": \"New York\", \"timeTaken\": 5000, \"responseLength\": 200}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userLocation").value("New York"));

        verify(analyticsService, times(1)).createLog(any(SurveyAnalyticsLog.class));
    }

    @Test
    void deleteLog_ShouldReturnNoContent() throws Exception {
        doNothing().when(analyticsService).deleteLog(1L);

        mockMvc.perform(delete("/api/v1/analytics/logs/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(analyticsService, times(1)).deleteLog(1L);
    }

    @Test
    void getLogsByUserLocation_ShouldReturnFilteredLogs() throws Exception {
        when(analyticsService.getLogsByUserLocation("New York")).thenReturn(Arrays.asList(sampleLog));

        mockMvc.perform(get("/api/v1/analytics/logs/location/New York")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userLocation").value("New York"));

        verify(analyticsService, times(1)).getLogsByUserLocation("New York");
    }

    @Test
    void getAllMetrics_ShouldReturnAllMetrics() throws Exception {
        when(surveyAnalyticsService.getAllMetrics()).thenReturn(Arrays.asList(sampleMetrics));

        mockMvc.perform(get("/api/v1/analytics/metrics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].totalResponses").value(100))
                .andExpect(jsonPath("$[0].popularIndustries.Technology").value(40));

        verify(surveyAnalyticsService, times(1)).getAllMetrics();
    }

    @Test
    void getMetricsById_WhenMetricsExist_ShouldReturnMetrics() throws Exception {
        when(surveyAnalyticsService.getMetricsById(1L)).thenReturn(Optional.of(sampleMetrics));

        mockMvc.perform(get("/api/v1/analytics/metrics/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalResponses").value(100));

        verify(surveyAnalyticsService, times(1)).getMetricsById(1L);
    }

    @Test
    void saveMetrics_ShouldReturnSavedMetrics() throws Exception {
        when(surveyAnalyticsService.createMetrics(any(SurveyMetrics.class))).thenReturn(sampleMetrics);

        mockMvc.perform(post("/api/v1/analytics/metrics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"totalResponses\": 100, \"averageCompanySize\": 500}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalResponses").value(100));

        verify(surveyAnalyticsService, times(1)).createMetrics(any(SurveyMetrics.class));
    }

    @Test
    void trackPageView_ShouldReturnOk() throws Exception {
        doNothing().when(analyticsService).trackPageView("homepage");

        mockMvc.perform(post("/api/v1/analytics/trackPageView")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"page\": \"homepage\"}"))
                .andExpect(status().isOk());

        verify(analyticsService, times(1)).trackPageView("homepage");
    }

    @Test
    void trackSurveyCompletion_ShouldReturnOk() throws Exception {
        doNothing().when(analyticsService).trackSurveyCompletion(anyString(), anyLong(), anyInt());

        mockMvc.perform(post("/api/v1/analytics/trackSurveyCompletion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userLocation\": \"New York\", \"timeTaken\": 5000, \"responseLength\": 200}"))
                .andExpect(status().isOk());

        verify(analyticsService, times(1)).trackSurveyCompletion("New York", 5000L, 200);
    }

    @Test
    void updateIndustryMetrics_ShouldReturnOk() throws Exception {
        doNothing().when(surveyAnalyticsService).updateIndustryMetrics("Technology");

        mockMvc.perform(post("/api/v1/analytics/updateIndustryMetrics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"industry\": \"Technology\"}"))
                .andExpect(status().isOk());

        verify(surveyAnalyticsService, times(1)).updateIndustryMetrics("Technology");
    }

    @Test
    void getAnalyticsSummary_ShouldReturnSummary() throws Exception {
        when(analyticsService.getAnalyticsSummary()).thenReturn(sampleSummary);

        mockMvc.perform(get("/api/v1/analytics/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLogs").value(150))
                .andExpect(jsonPath("$.averageTimeTaken").value(4500.0))
                .andExpect(jsonPath("$.popularIndustries.Technology").value(40));

        verify(analyticsService, times(1)).getAnalyticsSummary();
    }
}
