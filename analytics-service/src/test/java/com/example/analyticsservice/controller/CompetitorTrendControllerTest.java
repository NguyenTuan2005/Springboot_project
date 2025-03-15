package com.example.analyticsservice.controller;

import com.example.analyticsservice.model.CompetitorTrend;
import com.example.analyticsservice.service.CompetitorTrendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CompetitorTrendControllerTest {

    @Mock
    private CompetitorTrendService competitorTrendService;

    @InjectMocks
    private CompetitorTrendController competitorTrendController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(competitorTrendController).build();
    }

    @Test
    public void testGetTrendsByKeyword_Success() {
        // Given
        String keyword = "CRM software";
        List<CompetitorTrend> trends = Arrays.asList(
                createTrend(1L, keyword, 85.0, LocalDate.now()),
                createTrend(2L, keyword, 80.0, LocalDate.now().minusDays(7))
        );

        when(competitorTrendService.getTrendsForKeyword(keyword)).thenReturn(trends);

        // When
        ResponseEntity<List<CompetitorTrend>> response = competitorTrendController.getTrendsByKeyword(keyword);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trends, response.getBody());
        verify(competitorTrendService).getTrendsForKeyword(keyword);
    }

    @Test
    public void testGetTrendsByKeyword_NoContent() {
        // Given
        String keyword = "Nonexistent keyword";
        List<CompetitorTrend> emptyList = new ArrayList<>();

        when(competitorTrendService.getTrendsForKeyword(keyword)).thenReturn(emptyList);

        // When
        ResponseEntity<List<CompetitorTrend>> response = competitorTrendController.getTrendsByKeyword(keyword);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(competitorTrendService).getTrendsForKeyword(keyword);
    }

    @Test
    public void testTriggerFetchTrends_Success() {
        // Given
        doNothing().when(competitorTrendService).fetchAndStoreTrends();

        // When
        ResponseEntity<String> response = competitorTrendController.triggerFetchTrends();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Trend fetch process started successfully", response.getBody());
        verify(competitorTrendService).fetchAndStoreTrends();
    }

    @Test
    public void testTriggerFetchTrends_Error() {
        // Given
        doThrow(new RuntimeException("Script error")).when(competitorTrendService).fetchAndStoreTrends();

        // When
        ResponseEntity<String> response = competitorTrendController.triggerFetchTrends();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error starting trend fetch: Script error", response.getBody());
        verify(competitorTrendService).fetchAndStoreTrends();
    }

    @Test
    public void testGetTrendsByKeyword_MVC() throws Exception {
        // Given
        String keyword = "CRM software";
        List<CompetitorTrend> trends = Arrays.asList(
                createTrend(1L, keyword, 85.0, LocalDate.now()),
                createTrend(2L, keyword, 80.0, LocalDate.now().minusDays(7))
        );

        when(competitorTrendService.getTrendsForKeyword(keyword)).thenReturn(trends);

        // When/Then
        mockMvc.perform(get("/api/v1/competitors/{keyword}", keyword))
                .andExpect(status().isOk());
    }

    @Test
    public void testTriggerFetchTrends_MVC() throws Exception {
        // Given
        doNothing().when(competitorTrendService).fetchAndStoreTrends();

        // When/Then
        mockMvc.perform(post("/api/v1/competitors/fetch"))
                .andExpect(status().isOk())
                .andExpect(content().string("Trend fetch process started successfully"));
    }

    // Helper method
    private CompetitorTrend createTrend(Long id, String keyword, Double trendScore, LocalDate date) {
        CompetitorTrend trend = new CompetitorTrend();
        trend.setId(id);
        trend.setKeyword(keyword);
        trend.setTrendScore(trendScore);
        trend.setDate(date);
        return trend;
    }
}
