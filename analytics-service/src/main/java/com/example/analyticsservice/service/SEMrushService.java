package com.example.analyticsservice.service;

import com.example.analyticsservice.dto.SEMrushDataDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class SEMrushService {

//    @Value("${semrush.api.key}")
    private String apiKey = null;

    private static final String SEMRUSH_API_URL = "https://api.semrush.com/analytics/v1/";
    private final RestTemplate restTemplate = new RestTemplate();

    public SEMrushDataDTO getDomainAnalytics(String domain) {
        try {
            String encodedDomain = URLEncoder.encode(domain, StandardCharsets.UTF_8);
            String url = String.format("%s?type=domain_ranks&key=%s&domain=%s&database=us",
                    SEMRUSH_API_URL, apiKey, encodedDomain);

            return restTemplate.getForObject(url, SEMrushDataDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching SEMrush data: " + e.getMessage());
        }
    }
}