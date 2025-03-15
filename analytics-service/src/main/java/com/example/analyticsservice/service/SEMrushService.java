package com.example.analyticsservice.service;

import com.example.analyticsservice.dto.SEMrushDataDTO;

public interface SEMrushService {

    String buildUrl(String domain);

    SEMrushDataDTO getDomainAnalytics(String domain);
}