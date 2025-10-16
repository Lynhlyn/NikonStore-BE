package com.example.nikonbe.config.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    ConfigurableEnvironment environment = applicationContext.getEnvironment();

    try {
      Dotenv dotenv = Dotenv.configure().directory("./").filename(".env").ignoreIfMissing().load();

      Map<String, Object> envProperties = new HashMap<>();
      dotenv
          .entries()
          .forEach(
              entry -> {
                envProperties.put(entry.getKey(), entry.getValue());
              });

      environment
          .getPropertySources()
          .addFirst(new MapPropertySource("dotenvProperties", envProperties));

    } catch (Exception e) {
      System.err.println("Could not load .env file: " + e.getMessage());
    }
  }
}
