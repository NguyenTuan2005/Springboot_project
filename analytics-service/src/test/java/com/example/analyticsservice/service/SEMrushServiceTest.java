package com.example.analyticsservice.service;

import com.example.analyticsservice.dto.SEMrushDataDTO;
import com.example.analyticsservice.service.impl.SEMrushServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SEMrushServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private SEMrushServiceImpl semrushService;
    private static final String TEST_DOMAIN = "example.com";
    private static final String TEST_API_KEY = "test-api-key";

    @BeforeEach
    void setUp() {
        semrushService = new SEMrushServiceImpl(restTemplate);
        semrushService.setApiKey(TEST_API_KEY);
    }

    @Test
    void testBuildUrl() {
        // Given
        String expectedUrl = "https://api.semrush.com/analytics/v1/?type=domain_ranks&key=test-api-key&domain=example.com&database=us";

        // When
        String actualUrl = semrushService.buildUrl(TEST_DOMAIN);

        // Then
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void testBuildUrlWithSpecialCharacters() {
        // Given
        String domainWithSpecialChars = "example & test.com";
        String encodedDomain = URLEncoder.encode(domainWithSpecialChars, StandardCharsets.UTF_8);
        String expectedUrl = "https://api.semrush.com/analytics/v1/?type=domain_ranks&key=test-api-key&domain="
                + encodedDomain + "&database=us";

        // When
        String actualUrl = semrushService.buildUrl(domainWithSpecialChars);

        // Then
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void testGetDomainAnalytics_Success() {
        // Given
        SEMrushDataDTO expectedData = new SEMrushDataDTO();
        expectedData.setTraffic(10000L);
        expectedData.setKeywords(String.valueOf(5000L));
        expectedData.setTrafficCost(2500.0);

        String url = semrushService.buildUrl(TEST_DOMAIN);
        when(restTemplate.getForObject(url, SEMrushDataDTO.class)).thenReturn(expectedData);

        // When
        SEMrushDataDTO actualData = semrushService.getDomainAnalytics(TEST_DOMAIN);

        // Then
        assertNotNull(actualData);
        assertEquals(expectedData.getTraffic(), actualData.getTraffic());
        assertEquals(expectedData.getKeywords(), actualData.getKeywords());
        assertEquals(expectedData.getTrafficCost(), actualData.getTrafficCost());
        verify(restTemplate, times(1)).getForObject(url, SEMrushDataDTO.class);
    }

    @Test
    void testGetDomainAnalytics_NullApiKey() {
        // Given
        semrushService.setApiKey(null);

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            semrushService.getDomainAnalytics(TEST_DOMAIN);
        });

        assertTrue(exception.getMessage().contains("Error fetching SEMrush data"));
    }

    @Test
    void testGetDomainAnalytics_RestTemplateThrowsException() {
        // Given
        String url = semrushService.buildUrl(TEST_DOMAIN);
        when(restTemplate.getForObject(url, SEMrushDataDTO.class))
                .thenThrow(new RuntimeException("API connection failed"));

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            semrushService.getDomainAnalytics(TEST_DOMAIN);
        });

        assertTrue(exception.getMessage().contains("Error fetching SEMrush data"));
        assertTrue(exception.getMessage().contains("API connection failed"));
        verify(restTemplate, times(1)).getForObject(url, SEMrushDataDTO.class);
    }

    @Test
    void testGetDomainAnalytics_NullDomain() {
        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            semrushService.getDomainAnalytics(null);
        });

        assertTrue(exception.getMessage().contains("Error fetching SEMrush data"));
    }
}
