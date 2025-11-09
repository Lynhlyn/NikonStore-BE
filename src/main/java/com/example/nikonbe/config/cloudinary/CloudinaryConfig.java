package com.example.nikonbe.config.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CloudinaryConfig {

  @Value("${cloudinary.cloud-name}")
  private String cloudName;

  @Value("${cloudinary.api-key}")
  private String apiKey;

  @Value("${cloudinary.api-secret}")
  private String apiSecret;

  @Bean
  public Cloudinary cloudinary() {
    log.info("Initializing Cloudinary configuration for cloud: {}", cloudName);
    Cloudinary cloudinary =
        new Cloudinary(
            ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true));
    try {
      cloudinary.api().usage(ObjectUtils.emptyMap());
      log.info("Cloudinary connection established successfully");
    } catch (Exception e) {
      log.error("Failed to establish Cloudinary connection: {}", e.getMessage());
      throw new RuntimeException("Không thể kết nối đến Cloudinary: " + e.getMessage(), e);
    }
    return cloudinary;
  }
}
