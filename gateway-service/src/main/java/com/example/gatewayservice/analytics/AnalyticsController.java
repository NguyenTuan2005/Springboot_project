package com.example.gatewayservice.analytics;

import com.example.gatewayservice.config.ViewProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class AnalyticsController {

    private final ViewProperties viewProperties;

    @GetMapping("/analytics-service")
    public Mono<String> getAnalyticsPage() {
        return Mono.just(viewProperties.getAnalyticsPrefix() +"analytics-service");
    }
}
