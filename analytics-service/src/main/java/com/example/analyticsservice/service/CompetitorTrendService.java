package com.example.analyticsservice.service;

import com.example.analyticsservice.model.CompetitorTrend;
import com.example.analyticsservice.repository.CompetitorTrendRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CompetitorTrendService {

    private final CompetitorTrendRepository competitorTrendRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CompetitorTrendService(CompetitorTrendRepository competitorTrendRepository) {
        this.competitorTrendRepository = competitorTrendRepository;
    }

    public List<CompetitorTrend> getTrendsForKeyword(String keyword) {
        return competitorTrendRepository.findByKeywordOrderByDateDesc(keyword);
    }

    @SneakyThrows
    @Scheduled(cron = "0 0 0 * * SUN") // Run every Sunday at midnight
    public void fetchAndStoreTrends() {
        String[] keywords = {"CRM software", "ERP software", "Project management software"};
        String pythonInterpreter = "C:\\Users\\ADMIN\\.virtualenvs\\python\\Scripts\\python.exe";
        String scriptPath = "E:\\intelliJWorkspace\\springboot_project\\python-module\\scripts\\fetch_trends.py";
        for (String keyword : keywords) {
            ProcessBuilder pb = new ProcessBuilder(pythonInterpreter,
                    scriptPath, keyword);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            reader.lines().forEach(line -> log.debug("Error Stream: {}", line));

            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Optional<String> line = reader.lines()
                    .filter(l -> l.contains("keyword"))
                    .findFirst();

            line.ifPresent(l -> {
                if (l.contains("error")) {
                    handleError(l);
                } else {
                    handleCompetitorTrend(l);
                }
            });

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Python script exited with code " + exitCode);
            }
        }
    }

    private void handleCompetitorTrend(String line) {
        try {
            objectMapper.registerModule(new JavaTimeModule());
            CompetitorTrend trend = objectMapper.readValue(line, CompetitorTrend.class);
            competitorTrendRepository.save(trend);
        } catch (IOException e) {
            log.error("Error processing competitor trend", e);
        }
    }

    private void handleError(String line) {
        try {
            JsonNode errorNode = objectMapper.readTree(line);
            String errorMessage = errorNode.get("error").asText();
            log.error("Error detected: {}", errorMessage);
        } catch (IOException e) {
            log.error("Error reading error message", e);
        }
    }
}

