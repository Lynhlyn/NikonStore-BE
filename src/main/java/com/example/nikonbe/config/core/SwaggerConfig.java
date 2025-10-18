package com.example.nikonbe.config.core;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("API Docs for Nikon Store Management System")
                .version("1.0")
                .description("API tài liệu cho hệ thống e-commerce quản lý cửa hàng Nikon"));
  }
}
