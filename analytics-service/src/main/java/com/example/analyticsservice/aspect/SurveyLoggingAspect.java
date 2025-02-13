package com.example.analyticsservice.aspect;

import com.example.analyticsservice.model.SurveyAnalyticsLog;
import com.example.analyticsservice.repository.SurveyAnalyticsLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Aspect
@Component
public class SurveyLoggingAspect {

    private final SurveyAnalyticsLogRepository logRepository;

    public SurveyLoggingAspect(SurveyAnalyticsLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Around("@annotation(com.example.analyticsservice.annotation.Loggable)")
    public Object logSurveySubmission(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;

        String userLocation = mockUserLocation();
        int responseLength = result.toString().length();

        SurveyAnalyticsLog log = new SurveyAnalyticsLog();
        log.setTimeTaken(timeTaken);
        log.setUserLocation(userLocation);
        log.setResponseLength(responseLength);
        log.setTimestamp(LocalDateTime.now());

        logRepository.save(log);

        return result;
    }

    private String mockUserLocation() {
        String[] locations = {"New York", "London", "Tokyo", "Sydney", "Paris"};
        return locations[new Random().nextInt(locations.length)];
    }
}

