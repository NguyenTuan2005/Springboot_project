package com.example.analyticsservice.service;

import com.example.analyticsservice.model.CompetitorTrend;
import com.example.analyticsservice.repository.CompetitorTrendRepository;
import com.github.elibus.gtrends.api.GoogleTrendsClient;
import com.github.elibus.gtrends.api.exceptions.GoogleTrendsClientException;
import com.github.elibus.gtrends.api.request.InterestOverTimeRequest;
import com.github.elibus.gtrends.api.response.InterestOverTimeResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CompetitorTrendService {

    private final CompetitorTrendRepository competitorTrendRepository;

    private final GoogleTrendsClient googleTrendsClient = new GoogleTrendsClient();

    public CompetitorTrendService(CompetitorTrendRepository competitorTrendRepository) {
        this.competitorTrendRepository = competitorTrendRepository;
    }

    public List<CompetitorTrend> getTrendsForKeyword(String keyword) {
        return competitorTrendRepository.findByKeywordOrderByDateDesc(keyword);
    }

    @Scheduled(cron = "0 0 0 * * SUN") // Run every Sunday at midnight
    public void fetchAndStoreTrends() {
        String[] keywords = {"CRM software", "ERP software", "Project management software"};
        for (String keyword : keywords) {
            try {
                InterestOverTimeRequest request = InterestOverTimeRequest.builder()
                        .keyword(keyword)
                        .build();
                InterestOverTimeResponse response = googleTrendsClient.interestOverTime(request);

                int trendScore = response.getInterestOverTime().get(0).getValue();

                CompetitorTrend trend = new CompetitorTrend();
                trend.setKeyword(keyword);
                trend.setTrendScore(trendScore);
                trend.setDate(LocalDate.now());

                competitorTrendRepository.save(trend);
            } catch (GoogleTrendsClientException e) {
                // Handle exception (log it, send notification, etc.)
                e.printStackTrace();
            }
        }
    }
}

