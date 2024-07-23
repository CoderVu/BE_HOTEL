package com.example.booking_hotel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("http://localhost:3000"); // Specify your frontend URL
        config.addAllowedOriginPattern("https://codervu.github.io"); // Specify your frontend URL
        config.addAllowedHeader("*"); // Allow any header
        config.addAllowedMethod("*"); // Allow any method (GET, POST, etc.)
        config.setMaxAge(3600L); // Max age for preflight requests

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
