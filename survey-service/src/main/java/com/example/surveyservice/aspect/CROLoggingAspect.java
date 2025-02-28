package com.example.surveyservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CROLoggingAspect {
    @Around("execution(* com.example.surveyservice.controller.CROController.*(..))")
    public Object logCROInteractions(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("CRO interaction started: {}", joinPoint.getSignature().getName());
        try {
            Object result = joinPoint.proceed();
            log.info("CRO interaction completed successfully");
            return result;
        } catch (Exception e) {
            log.error("CRO interaction failed", e);
            throw e;
        }
    }
}