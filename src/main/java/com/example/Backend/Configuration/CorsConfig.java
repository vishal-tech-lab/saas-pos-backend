package com.example.Backend.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {

        CorsConfiguration config =
                new CorsConfiguration();

        // ALLOW FRONTEND

        config.addAllowedOrigin(
                "http://localhost:5173"
        );

        config.addAllowedOrigin(
                "https://nexgenpos.netlify.app"
        );
config.addAllowedOrigin(
    "https://www.nexgenpos.netlify.app"
);
        // ALLOW METHODS

        config.addAllowedMethod("*");

        // ALLOW HEADERS

        config.addAllowedHeader("*");

        // ALLOW CREDENTIALS

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                config
        );

        return new CorsFilter(source);
    }
}