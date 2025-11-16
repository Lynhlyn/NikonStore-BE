package com.example.nikonbe.modules.vnpay.service.impl;

import com.example.nikonbe.modules.vnpay.config.VNPayConfig;
import com.example.nikonbe.modules.vnpay.service.interF.VNPayService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VNPayServiceImpl implements VNPayService {

  private final VNPayConfig config;

  @Override
  public boolean verifyReturn(Map<String, String> params) {
    if (config.getHashSecret() == null || config.getHashSecret().trim().isEmpty()) {
      log.error("VNPAY_HASH_SECRET chưa được cấu hình. Không thể verify callback.");
      throw new IllegalStateException("VNPAY_HASH_SECRET chưa được cấu hình");
    }
    String secureHash = params.get("vnp_SecureHash");
    params.remove("vnp_SecureHash");
    params.remove("vnp_SecureHashType");

    List<String> fieldNames = new ArrayList<>(params.keySet());
    Collections.sort(fieldNames);
    StringBuilder hashData = new StringBuilder();
    for (Iterator<String> it = fieldNames.iterator(); it.hasNext(); ) {
      String fieldName = it.next();
      String fieldValue = params.get(fieldName);
      if (fieldValue != null && fieldValue.length() > 0) {
        hashData
            .append(fieldName)
            .append('=')
            .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
        if (it.hasNext()) {
          hashData.append('&');
        }
      }
    }

    String calculatedHash = hmacSHA512(config.getHashSecret(), hashData.toString());
    return secureHash.equals(calculatedHash);
  }

  @Override
  public String createPaymentUrl(long amount, String orderInfo, String ipAddr, String orderId) {
    if (config.getTmnCode() == null || config.getTmnCode().trim().isEmpty()) {
      log.error("VNPAY_TMN_CODE chưa được cấu hình. Không thể tạo payment URL.");
      throw new IllegalStateException("VNPAY_TMN_CODE chưa được cấu hình");
    }
    if (config.getHashSecret() == null || config.getHashSecret().trim().isEmpty()) {
      log.error("VNPAY_HASH_SECRET chưa được cấu hình. Không thể tạo payment URL.");
      throw new IllegalStateException("VNPAY_HASH_SECRET chưa được cấu hình");
    }
    Map<String, String> vnpParams = new HashMap<>();
    vnpParams.put("vnp_Version", "2.1.0");
    vnpParams.put("vnp_Command", "pay");
    vnpParams.put("vnp_TmnCode", config.getTmnCode());
    vnpParams.put("vnp_Amount", String.valueOf(amount * 100));
    vnpParams.put("vnp_CurrCode", "VND");
    vnpParams.put("vnp_TxnRef", orderId);
    vnpParams.put("vnp_OrderInfo", orderInfo);
    vnpParams.put("vnp_OrderType", "other");
    vnpParams.put("vnp_Locale", "vn");
    vnpParams.put("vnp_ReturnUrl", config.getReturnUrl());
    vnpParams.put("vnp_IpAddr", ipAddr);
    vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

    List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
    Collections.sort(fieldNames);
    StringBuilder hashData = new StringBuilder();
    StringBuilder query = new StringBuilder();
    for (Iterator<String> it = fieldNames.iterator(); it.hasNext(); ) {
      String fieldName = it.next();
      String fieldValue = vnpParams.get(fieldName);
      if (fieldValue != null && fieldValue.length() > 0) {
        String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);
        hashData.append(fieldName).append('=').append(encodedValue);
        query
            .append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8))
            .append('=')
            .append(encodedValue);
        if (it.hasNext()) {
          hashData.append('&');
          query.append('&');
        }
      }
    }

    String secureHash = hmacSHA512(config.getHashSecret(), hashData.toString());
    query.append("&vnp_SecureHash=").append(secureHash);

    return config.getPayUrl() + "?" + query.toString();
  }

  @Override
  public String generateQrCode(String paymentUrl) {
    try {
      if (paymentUrl == null || paymentUrl.trim().isEmpty()) {
        throw new IllegalArgumentException("Payment URL không được để trống");
      }

      log.info("Attempting to generate VNPay QR code via API from payment URL: {}", paymentUrl);

      String vnPayQrCode = tryGenerateVnPayQrCode(paymentUrl);
      if (vnPayQrCode != null && !vnPayQrCode.trim().isEmpty()) {
        log.info("Successfully generated VNPay QR code via API");
        return vnPayQrCode;
      }

      log.warn("VNPay QR API failed, falling back to payment URL QR code");
      return generatePaymentUrlQrCode(paymentUrl);
    } catch (Exception e) {
      log.error("Error generating QR code: {}", e.getMessage(), e);
      log.warn("Falling back to payment URL QR code due to error");
      return generatePaymentUrlQrCode(paymentUrl);
    }
  }

  private String tryGenerateVnPayQrCode(String paymentUrl) {
    try {
      if (config.getTmnCode() == null || config.getTmnCode().trim().isEmpty()) {
        return null;
      }
      if (config.getHashSecret() == null || config.getHashSecret().trim().isEmpty()) {
        return null;
      }
      if (config.getApiUrl() == null || config.getApiUrl().trim().isEmpty()) {
        return null;
      }

      Map<String, String> paymentParams = parsePaymentUrl(paymentUrl);
      if (paymentParams.isEmpty()) {
        return null;
      }

      Map<String, String> params = new HashMap<>();
      params.put("vnp_Version", paymentParams.getOrDefault("vnp_Version", "2.1.0"));
      params.put("vnp_Command", "qrpay");
      params.put("vnp_TmnCode", config.getTmnCode());
      params.put("vnp_Amount", paymentParams.get("vnp_Amount"));
      params.put("vnp_CurrCode", paymentParams.getOrDefault("vnp_CurrCode", "VND"));
      params.put("vnp_TxnRef", paymentParams.get("vnp_TxnRef"));
      params.put("vnp_OrderInfo", paymentParams.get("vnp_OrderInfo"));
      params.put("vnp_OrderType", paymentParams.getOrDefault("vnp_OrderType", "other"));
      params.put("vnp_Locale", paymentParams.getOrDefault("vnp_Locale", "vn"));
      String returnUrl = config.getReturnUrl();
      params.put("vnp_ReturnUrl", returnUrl);
      String ipAddr = normalizeIpAddress(paymentParams.get("vnp_IpAddr"));
      params.put("vnp_IpAddr", ipAddr);
      params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

      List<String> fieldNames = new ArrayList<>(params.keySet());
      Collections.sort(fieldNames);
      StringBuilder hashData = new StringBuilder();
      StringBuilder query = new StringBuilder();

      List<String> validFields = new ArrayList<>();
      for (String fieldName : fieldNames) {
        String fieldValue = params.get(fieldName);
        if (fieldValue != null && fieldValue.length() > 0) {
          validFields.add(fieldName);
        }
      }

      for (int i = 0; i < validFields.size(); i++) {
        String fieldName = validFields.get(i);
        String fieldValue = params.get(fieldName);
        String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);
        hashData.append(fieldName).append('=').append(encodedValue);
        query
            .append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8))
            .append('=')
            .append(encodedValue);
        if (i < validFields.size() - 1) {
          hashData.append('&');
          query.append('&');
        }
      }

      String secureHash = hmacSHA512(config.getHashSecret(), hashData.toString());
      query.append("&vnp_SecureHash=").append(secureHash);
      query.append("&vnp_SecureHashType=SHA512");

      String requestBody = query.toString();
      log.info("Calling VNPay QR API: {}", config.getApiUrl());

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(config.getApiUrl()))
              .header("Content-Type", "application/x-www-form-urlencoded")
              .timeout(Duration.ofSeconds(30))
              .POST(HttpRequest.BodyPublishers.ofString(requestBody))
              .build();

      HttpClient httpClient =
          HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      int statusCode = response.statusCode();
      String responseBody = response.body();

      if (statusCode == 200) {
        Map<String, String> responseParams = parseResponse(responseBody);
        String responseCode = responseParams.get("vnp_ResponseCode");

        if ("00".equals(responseCode)) {
          String qrCode = responseParams.get("vnp_QrCode");
          if (qrCode != null && !qrCode.trim().isEmpty()) {
            return qrCode;
          }
        }
      }

      log.warn("VNPay QR API returned status: {}, body: {}", statusCode, responseBody);
      return null;
    } catch (Exception e) {
      log.warn("VNPay QR API call failed: {}", e.getMessage());
      return null;
    }
  }

  private String generatePaymentUrlQrCode(String paymentUrl) {
    try {
      log.info("Generating QR code from payment URL directly");

      Map<EncodeHintType, Object> hints = new HashMap<>();
      hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
      hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
      hints.put(EncodeHintType.MARGIN, 1);

      QRCodeWriter qrCodeWriter = new QRCodeWriter();
      BitMatrix bitMatrix = qrCodeWriter.encode(paymentUrl, BarcodeFormat.QR_CODE, 300, 300, hints);

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
      byte[] qrCodeBytes = outputStream.toByteArray();

      String base64QrCode = Base64.getEncoder().encodeToString(qrCodeBytes);
      String qrCodeDataUri = "data:image/png;base64," + base64QrCode;

      log.info("Successfully generated QR code from payment URL");
      return qrCodeDataUri;
    } catch (Exception e) {
      log.error("Error generating QR code from payment URL: {}", e.getMessage(), e);
      throw new RuntimeException("Không thể tạo QR code: " + e.getMessage(), e);
    }
  }

  private Map<String, String> parsePaymentUrl(String paymentUrl) {
    Map<String, String> params = new HashMap<>();
    try {
      int queryIndex = paymentUrl.indexOf('?');
      if (queryIndex < 0) {
        return params;
      }
      String query = paymentUrl.substring(queryIndex + 1);
      String[] pairs = query.split("&");
      for (String pair : pairs) {
        int eqIndex = pair.indexOf('=');
        if (eqIndex > 0) {
          String key = URLDecoder.decode(pair.substring(0, eqIndex), StandardCharsets.UTF_8);
          String value = URLDecoder.decode(pair.substring(eqIndex + 1), StandardCharsets.UTF_8);
          params.put(key, value);
        }
      }
    } catch (Exception e) {
      log.warn("Error parsing payment URL: {}", e.getMessage());
    }
    return params;
  }

  private Map<String, String> parseResponse(String responseBody) {
    Map<String, String> params = new HashMap<>();
    try {
      if (responseBody == null || responseBody.trim().isEmpty()) {
        return params;
      }

      responseBody = responseBody.trim();

      if (responseBody.startsWith("{")) {
        try {
          ObjectMapper mapper = new ObjectMapper();
          JsonNode jsonNode = mapper.readTree(responseBody);
          jsonNode
              .fields()
              .forEachRemaining(
                  entry -> {
                    params.put(entry.getKey(), entry.getValue().asText());
                  });
        } catch (Exception e) {
          log.warn("Error parsing JSON response, trying query string format: {}", e.getMessage());
          return parseQueryString(responseBody);
        }
      } else {
        return parseQueryString(responseBody);
      }
    } catch (Exception e) {
      log.warn("Error parsing response: {}", e.getMessage());
    }
    return params;
  }

  private Map<String, String> parseQueryString(String queryString) {
    Map<String, String> params = new HashMap<>();
    try {
      String[] pairs = queryString.split("&");
      for (String pair : pairs) {
        int eqIndex = pair.indexOf('=');
        if (eqIndex > 0) {
          String key = URLDecoder.decode(pair.substring(0, eqIndex), StandardCharsets.UTF_8);
          String value = URLDecoder.decode(pair.substring(eqIndex + 1), StandardCharsets.UTF_8);
          params.put(key, value);
        }
      }
    } catch (Exception e) {
      log.warn("Error parsing query string: {}", e.getMessage());
    }
    return params;
  }

  private String normalizeIpAddress(String ipAddr) {
    if (ipAddr == null || ipAddr.trim().isEmpty()) {
      return "127.0.0.1";
    }
    ipAddr = ipAddr.trim();
    if ("0:0:0:0:0:0:0:1".equals(ipAddr) || "::1".equals(ipAddr)) {
      return "127.0.0.1";
    }
    if (ipAddr.startsWith("0:0:0:0:0:0:") || ipAddr.contains("::")) {
      return "127.0.0.1";
    }
    return ipAddr;
  }

  private String hmacSHA512(String key, String data) {
    try {
      Mac hmac512 = Mac.getInstance("HmacSHA512");
      SecretKeySpec secretKey =
          new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
      hmac512.init(secretKey);
      byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
      StringBuilder hash = new StringBuilder();
      for (byte b : bytes) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) hash.append('0');
        hash.append(hex);
      }
      return hash.toString();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
