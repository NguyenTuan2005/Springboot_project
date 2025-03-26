package com.example.gatewayservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app.view")
public class ViewProperties {
    private String surveyPrefix;
    private String analyticsPrefix;
}
