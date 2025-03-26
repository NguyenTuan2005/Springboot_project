package com.example.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("survey-service", r -> r.path("/api/v1/**")
//                        .filters(f -> f.rewritePath("/api/surveys(?<segment>/?.*)", "/api/v1/surveys${segment}"))
                        .uri("http://localhost:8082"))
                .route("analytics-service", r -> r.path("/api/analytics/**")
                        .filters(f -> f.rewritePath("/api/v1/analytics/(?<segment>/?.*)", "/api/v1/analytics/${segment}"))
                        .uri("http://localhost:8081"))
                .build();
    }
}
