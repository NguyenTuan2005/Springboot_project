package com.example.analyticsservice.service;

import com.example.analyticsservice.model.CompetitorTrend;
import com.example.analyticsservice.repository.CompetitorTrendRepository;
import com.example.analyticsservice.service.impl.CompetitorTrendServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompetitorTrendServiceTest {

    @Mock
    private CompetitorTrendRepository competitorTrendRepository;

    @Mock
    private Process process;

    @Mock
    private ProcessBuilder processBuilder;

    private CompetitorTrendService competitorTrendService;
    private ObjectMapper objectMapper;
    private Supplier<ProcessBuilder> processBuilderSupplier;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        processBuilderSupplier = () -> processBuilder;

        competitorTrendService = new CompetitorTrendServiceImpl(
                competitorTrendRepository,
                objectMapper,
                processBuilderSupplier
        );
    }

    @Test
    public void testGetTrendsForKeyword() {
        // Given
        String keyword = "CRM software";
        LocalDate today = LocalDate.now();
        List<CompetitorTrend> expectedTrends = Arrays.asList(
                createTrend(1L, keyword, 85.0, today),
                createTrend(2L, keyword, 80.0, today.minusDays(7))
        );

        when(competitorTrendRepository.findByKeywordOrderByDateDesc(keyword)).thenReturn(expectedTrends);

        // When
        List<CompetitorTrend> actualTrends = competitorTrendService.getTrendsForKeyword(keyword);

        // Then
        assertEquals(expectedTrends, actualTrends);
        verify(competitorTrendRepository).findByKeywordOrderByDateDesc(keyword);
    }

    @Test
    public void testFetchAndStoreTrends_Success() throws Exception {
        // Given
        String validJson = "{\"keyword\":\"CRM software\",\"trendScore\":85,\"date\":\"2023-06-01\"}";
        setupProcessMock(validJson, 0);

        // When
        competitorTrendService.fetchAndStoreTrends();

        // Then
        ArgumentCaptor<CompetitorTrend> trendCaptor = ArgumentCaptor.forClass(CompetitorTrend.class);
        verify(competitorTrendRepository).save(trendCaptor.capture());

        CompetitorTrend savedTrend = trendCaptor.getValue();
        assertEquals("CRM software", savedTrend.getKeyword());
        assertEquals(85, savedTrend.getTrendScore());
        assertEquals(LocalDate.of(2023, 6, 1), savedTrend.getDate());
    }

    @Test
    public void testFetchAndStoreTrends_Error() throws Exception {
        // Given
        String errorJson = "{\"error\":\"API limit exceeded\"}";
        setupProcessMock(errorJson, 0);

        // When
        competitorTrendService.fetchAndStoreTrends();

        // Then
        verify(competitorTrendRepository, never()).save(any());
    }

    @Test
    public void testFetchAndStoreTrends_ProcessError() throws Exception {
        // Given
        setupProcessMock("", 1);

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            competitorTrendService.fetchAndStoreTrends();
        });
    }

    @Test
    public void testHandleCompetitorTrend() throws Exception {
        // Given
        String validJson = "{\"keyword\":\"ERP software\",\"trendScore\":75,\"date\":\"2023-06-01\"}";

        // Use reflection to access private method
        java.lang.reflect.Method method = CompetitorTrendServiceImpl.class.getDeclaredMethod("handleCompetitorTrend", String.class);
        method.setAccessible(true);

        // When
        method.invoke(competitorTrendService, validJson);

        // Then
        ArgumentCaptor<CompetitorTrend> trendCaptor = ArgumentCaptor.forClass(CompetitorTrend.class);
        verify(competitorTrendRepository).save(trendCaptor.capture());

        CompetitorTrend savedTrend = trendCaptor.getValue();
        assertEquals("ERP software", savedTrend.getKeyword());
        assertEquals(75, savedTrend.getTrendScore());
    }

    @Test
    public void testHandleError() throws Exception {
        // Given
        String errorJson = "{\"error\":\"Rate limit exceeded\"}";

        // Use reflection to access private method
        java.lang.reflect.Method method = CompetitorTrendServiceImpl.class.getDeclaredMethod("handleError", String.class);
        method.setAccessible(true);

        // When
        method.invoke(competitorTrendService, errorJson);

        // Then - No easy way to verify logging, but at least we ensure no exceptions
    }

    // Helper methods
    private CompetitorTrend createTrend(Long id, String keyword, Double trendScore, LocalDate date) {
        CompetitorTrend trend = new CompetitorTrend();
        trend.setId(id);
        trend.setKeyword(keyword);
        trend.setTrendScore(trendScore);
        trend.setDate(date);
        return trend;
    }

    private void setupProcessMock(String output, int exitCode) throws Exception {
        // Mock ProcessBuilder
        when(processBuilder.command(anyString(), anyString(), anyString())).thenReturn(processBuilder);
        when(processBuilder.start()).thenReturn(process);
        when(processBuilder.redirectErrorStream(anyBoolean())).thenReturn(processBuilder);

        // Mock Process
        InputStream inputStream = new ByteArrayInputStream(output.getBytes());
        InputStream errorStream = new ByteArrayInputStream("".getBytes());

        when(process.getInputStream()).thenReturn(inputStream);
        when(process.getErrorStream()).thenReturn(errorStream);
        when(process.waitFor()).thenReturn(exitCode);
    }
}
