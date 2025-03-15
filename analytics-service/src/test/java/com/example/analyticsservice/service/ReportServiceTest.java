package com.example.analyticsservice.service;

import com.example.analyticsservice.dto.SEMrushDataDTO;
import com.example.analyticsservice.model.CompetitorTrend;
import com.example.analyticsservice.service.impl.ReportServiceImpl;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;

import java.lang.reflect.Method;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private SEMrushService semrushService;

    @Mock
    private CompetitorTrendService competitorTrendService;

    @InjectMocks
    private ReportServiceImpl reportService;

    private ReportServiceImpl reportServiceSpy;

    private static final String TEST_DOMAIN = "example.com";

    @BeforeEach
    void setUp() {
        reportServiceSpy = spy(reportService);
    }

    @Test
    void testGetSemrushData() {
        // Given
        SEMrushDataDTO expectedData = new SEMrushDataDTO();
        expectedData.setTraffic(10000L);
        when(semrushService.getDomainAnalytics(TEST_DOMAIN)).thenReturn(expectedData);

        // When
        SEMrushDataDTO actualData = reportService.getSemrushData(TEST_DOMAIN);

        // Then
        assertNotNull(actualData);
        assertEquals(expectedData.getTraffic(), actualData.getTraffic());
        verify(semrushService, times(1)).getDomainAnalytics(TEST_DOMAIN);
    }

    @Test
    void testGetCompetitorTrends() {
        // Given
        List<CompetitorTrend> expectedTrends = Arrays.asList(
                CompetitorTrend.builder()
                        .trendScore(80.5).build(),
                CompetitorTrend.builder()
                        .trendScore(70.2).build()
        );
        when(competitorTrendService.getTrendsForKeyword(TEST_DOMAIN)).thenReturn(expectedTrends);

        // When - Use reflection to access protected method
        List<CompetitorTrend> actualTrends;
        try {
            Method getCompetitorTrendsMethod = ReportServiceImpl.class.getDeclaredMethod("getCompetitorTrends", String.class);
            getCompetitorTrendsMethod.setAccessible(true);
            actualTrends = (List<CompetitorTrend>) getCompetitorTrendsMethod.invoke(reportService, TEST_DOMAIN);
        } catch (Exception e) {
            fail("Failed to invoke getCompetitorTrends method via reflection: " + e.getMessage());
            return;
        }

        // Then
        assertNotNull(actualTrends);
        assertEquals(2, actualTrends.size());
        assertEquals(expectedTrends, actualTrends);
        verify(competitorTrendService, times(1)).getTrendsForKeyword(TEST_DOMAIN);
    }

    @Test
    void testCreatePdfDocument() throws IOException {
        // Given
        ReportServiceImpl reportServiceImpl = new ReportServiceImpl(semrushService, competitorTrendService);

        // When - Use reflection to access protected method
        PDDocument document = null;
        try {
            Method createPdfDocumentMethod = ReportServiceImpl.class.getDeclaredMethod("createPdfDocument");
            createPdfDocumentMethod.setAccessible(true);
            document = (PDDocument) createPdfDocumentMethod.invoke(reportServiceImpl);

            // Then
            assertNotNull(document);
            assertEquals(0, document.getNumberOfPages());
        } catch (Exception e) {
            fail("Failed to invoke createPdfDocument method via reflection: " + e.getMessage());
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }

    @Test
    void testAddContentToPdf() throws IOException {
        // Given
        PDDocument document = new PDDocument();
        SEMrushDataDTO semrushData = new SEMrushDataDTO();
        semrushData.setTraffic(10000L);
        semrushData.setKeywords(String.valueOf(5000L));
        semrushData.setTrafficCost(2500.0);

        List<CompetitorTrend> trends = Arrays.asList(
                CompetitorTrend.builder()
                        .trendScore(80.5).build(),
                CompetitorTrend.builder()
                        .trendScore(70.2).build()
        );

        // When
        reportService.addContentToPdf(document, TEST_DOMAIN, semrushData, trends);

        // Then
        assertEquals(1, document.getNumberOfPages());

        // Clean up
        document.close();
    }

    /*
    /Error
    @Test
    void testGenerateAnalyticsReport_Success() throws Exception {
        // Given
        SEMrushDataDTO semrushData = new SEMrushDataDTO();
        semrushData.setTraffic(10000L);
        semrushData.setKeywords(String.valueOf(5000L));
        semrushData.setTrafficCost(2500.0);

        List<CompetitorTrend> trends = Arrays.asList(
                CompetitorTrend.builder()
                        .trendScore(80.5).build(),
                CompetitorTrend.builder()
                        .trendScore(70.2).build()
        );

        // Create a mock PDDocument
        PDDocument mockDocument = mock(PDDocument.class);

        // Mock the methods that would normally interact with external services
        doReturn(semrushData).when(reportServiceSpy).getSemrushData(TEST_DOMAIN);
        doReturn(trends).when(reportServiceSpy).getCompetitorTrends(TEST_DOMAIN);
        doReturn(mockDocument).when(reportServiceSpy).createPdfDocument();
        doNothing().when(reportServiceSpy).addContentToPdf(
                eq(mockDocument), eq(TEST_DOMAIN), eq(semrushData), eq(trends)
        );

        ByteArrayOutputStream mockBaos = mock(ByteArrayOutputStream.class);
        byte[] expectedBytes = {1, 2, 3};
        when(mockBaos.toByteArray()).thenReturn(expectedBytes);

        PowerMockito.whenNew(ByteArrayOutputStream.class).withNoArguments().thenReturn(mockBaos);

        // When
        byte[] result = reportServiceSpy.generateAnalyticsReport(TEST_DOMAIN);

        // Then
        assertNotNull(result);
        assertArrayEquals(expectedBytes, result);
        verify(reportServiceSpy).getSemrushData(TEST_DOMAIN);
        verify(reportServiceSpy).getCompetitorTrends(TEST_DOMAIN);
        verify(reportServiceSpy).createPdfDocument();
        verify(reportServiceSpy).addContentToPdf(
                eq(mockDocument), eq(TEST_DOMAIN), eq(semrushData), eq(trends)
        );
        verify(mockDocument).save(mockBaos);
        verify(mockDocument).close();
        verify(mockBaos).close();
    }
     */

    @Test
    void testGenerateAnalyticsReport_SemrushServiceThrowsException() {
        // Given
        when(reportServiceSpy.getSemrushData(TEST_DOMAIN))
                .thenThrow(new RuntimeException("SEMrush data fetch failed"));

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            reportServiceSpy.generateAnalyticsReport(TEST_DOMAIN);
        });

        assertTrue(exception.getMessage().contains("Error generating PDF report"));
        assertTrue(exception.getMessage().contains("SEMrush data fetch failed"));
        verify(reportServiceSpy).getSemrushData(TEST_DOMAIN);
    }

    @Test
    void testGenerateAnalyticsReport_CompetitorServiceThrowsException() {
        // Given
        SEMrushDataDTO semrushData = new SEMrushDataDTO();
        doReturn(semrushData).when(reportServiceSpy).getSemrushData(TEST_DOMAIN);

        when(reportServiceSpy.getCompetitorTrends(TEST_DOMAIN))
                .thenThrow(new RuntimeException("Competitor trends fetch failed"));

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            reportServiceSpy.generateAnalyticsReport(TEST_DOMAIN);
        });

        assertTrue(exception.getMessage().contains("Error generating PDF report"));
        assertTrue(exception.getMessage().contains("Competitor trends fetch failed"));
        verify(reportServiceSpy).getSemrushData(TEST_DOMAIN);
        verify(reportServiceSpy).getCompetitorTrends(TEST_DOMAIN);
    }

    @Test
    void testGenerateAnalyticsReport_PDFCreationThrowsException() throws IOException {
        // Given
        SEMrushDataDTO semrushData = new SEMrushDataDTO();
        List<CompetitorTrend> trends = new ArrayList<>();

        doReturn(semrushData).when(reportServiceSpy).getSemrushData(TEST_DOMAIN);
        doReturn(trends).when(reportServiceSpy).getCompetitorTrends(TEST_DOMAIN);

        PDDocument mockDocument = mock(PDDocument.class);
        doReturn(mockDocument).when(reportServiceSpy).createPdfDocument();

        doThrow(new IOException("PDF creation failed"))
                .when(reportServiceSpy).addContentToPdf(any(), any(), any(), any());

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            reportServiceSpy.generateAnalyticsReport(TEST_DOMAIN);
        });

        assertTrue(exception.getMessage().contains("Error generating PDF report"));
        assertTrue(exception.getMessage().contains("PDF creation failed"));
        verify(reportServiceSpy).getSemrushData(TEST_DOMAIN);
        verify(reportServiceSpy).getCompetitorTrends(TEST_DOMAIN);
        verify(reportServiceSpy).createPdfDocument();
        verify(mockDocument).close();
    }
}
