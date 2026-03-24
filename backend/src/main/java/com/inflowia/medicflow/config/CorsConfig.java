package com.inflowia.medicflow.config;

import io.micrometer.common.lang.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public CorsConfig(@Value("${app.cors.allowed-origins:http://localhost:3000}") String allowedOriginsProperty) {
        this.allowedOrigins = Arrays.stream(allowedOriginsProperty.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .filter(origin -> !"*".equals(origin))
                .toArray(String[]::new);
    }

    @SuppressWarnings("null")
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Location")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
