package com.example.nikonbe.modules.vnpay.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "vnpay")
public class VNPayConfig {

  private String payUrl;
  private String returnUrl;
  private String tmnCode;
  private String hashSecret;
  private String apiUrl;

  @PostConstruct
  public void validate() {
    if (tmnCode == null || tmnCode.contains("${")) {
      log.error("VNPAY_TMN_CODE chưa được cấu hình đúng. Giá trị hiện tại: {}", tmnCode);
      throw new IllegalStateException(
          "VNPAY_TMN_CODE phải được cấu hình trong environment variables");
    }
    if (hashSecret == null || hashSecret.contains("${")) {
      log.error("VNPAY_HASH_SECRET chưa được cấu hình đúng");
      throw new IllegalStateException(
          "VNPAY_HASH_SECRET phải được cấu hình trong environment variables");
    }
    if (tmnCode.trim().isEmpty() || hashSecret.trim().isEmpty()) {
      log.warn(
          "VNPAY_TMN_CODE hoặc VNPAY_HASH_SECRET đang trống. VNPay sẽ không hoạt động cho đến khi được cấu hình đúng.");
    } else {
      log.info("VNPay config loaded successfully. TMN Code: {}", tmnCode);
    }
  }
}
