package com.example.analyticsservice.service;

import com.example.analyticsservice.model.CompetitorTrend;
import java.util.List;

public interface CompetitorTrendService {

    List<CompetitorTrend> getTrendsForKeyword(String keyword);

    void fetchAndStoreTrends();
}

