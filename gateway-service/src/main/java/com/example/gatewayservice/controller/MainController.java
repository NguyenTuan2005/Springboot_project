package com.example.gatewayservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class MainController {

    @GetMapping("/login")
    public Mono<String> getLoginPage() {
        return Mono.just("login");
    }

    @GetMapping("/dashboard")
    public Mono<String> getDashboardPage() {
        return Mono.just("index");
    }

    @GetMapping("/service")
    public Mono<String> getServicePage() {
        return Mono.just("service");
    }
}
