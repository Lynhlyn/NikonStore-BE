package com.example.nikonbe.config.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

  private static final String[] LOCALHOST_ORIGINS = {
    "http://localhost:3000",
    "https://localhost:3000",
    "http://127.0.0.1:3000",
    "https://127.0.0.1:3000",
    "http://127.0.0.1:5500",
    "http://localhost:8080"
  };

  private static final String[] DEPLOYMENT_ORIGINS = {
    "https://nikon-store-gamma.vercel.app",
    "https://nikon-store-gamma-dev.vercel.app",
    "https://*.trycloudflare.com"
  };

  private static final String[] DYNAMIC_PATTERNS = {
    "https://*.vercel.app",
    "https://*.netlify.app",
    "http://localhost:*",
    "https://*.trycloudflare.com",
    "https://localhost:*",
    "https://*.asse.devtunnels.ms"
  };

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    for (String origin : LOCALHOST_ORIGINS) {
      config.addAllowedOrigin(origin);
    }
    for (String origin : DEPLOYMENT_ORIGINS) {
      config.addAllowedOrigin(origin);
    }

    for (String pattern : DYNAMIC_PATTERNS) {
      config.addAllowedOriginPattern(pattern);
    }

    config.addAllowedMethod("GET");
    config.addAllowedMethod("POST");
    config.addAllowedMethod("PUT");
    config.addAllowedMethod("DELETE");
    config.addAllowedMethod("PATCH");
    config.addAllowedMethod("OPTIONS");
    config.addAllowedMethod("HEAD");

    config.addAllowedHeader("*");
    config.addAllowedHeader("Authorization");
    config.addAllowedHeader("Content-Type");
    config.addAllowedHeader("X-Requested-With");
    config.addAllowedHeader("Accept");
    config.addAllowedHeader("Origin");
    config.addAllowedHeader("Access-Control-Request-Method");
    config.addAllowedHeader("Access-Control-Request-Headers");

    config.setAllowCredentials(true);

    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
