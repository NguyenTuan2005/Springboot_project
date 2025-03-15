package com.example.analyticsservice.service.impl;

import com.example.analyticsservice.dto.SEMrushDataDTO;
import com.example.analyticsservice.service.SEMrushService;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Setter
public class SEMrushServiceImpl implements SEMrushService {

    //    @Value("${semrush.api.key}")
    private String apiKey = null;
    private static final String SEMRUSH_API_URL = "https://api.semrush.com/analytics/v1/";
    private final RestTemplate restTemplate;

    public SEMrushServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SEMrushServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String buildUrl(String domain) {
        String encodedDomain = URLEncoder.encode(domain, StandardCharsets.UTF_8);
        return String.format("%s?type=domain_ranks&key=%s&domain=%s&database=us",
                SEMRUSH_API_URL, apiKey, encodedDomain);
    }

    @Override
    public SEMrushDataDTO getDomainAnalytics(String domain) {
        if (this.apiKey == null) {
            throw new RuntimeException("Error fetching SEMrush data: API key is null");
        }
        try {
            String url = buildUrl(domain);
            return restTemplate.getForObject(url, SEMrushDataDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching SEMrush data: " + e.getMessage());
        }
    }
}
