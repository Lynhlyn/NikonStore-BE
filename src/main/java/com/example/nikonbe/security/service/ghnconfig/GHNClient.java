package com.example.nikonbe.security.service.ghnconfig;

import com.example.nikonbe.security.dto.request.ShippingFeeRequestDTO;
import com.example.nikonbe.security.dto.response.ShippingFeeResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GHNClient {
  @Value("${ghn.token}")
  private String token;

  @Value("${ghn.shopId}")
  private String shopId;

  private final RestTemplate restTemplate = new RestTemplate();

  public ShippingFeeResponseDTO calculateShippingFee(ShippingFeeRequestDTO req) {
    if (req.getToDistrictId() == null
        || req.getToWardCode() == null
        || req.getWeightKg() == null
        || req.getLength() == null
        || req.getWidth() == null
        || req.getHeight() == null) {
      return new ShippingFeeResponseDTO(0, "Missing required fields");
    }
    int serviceId = 53321;
    int fromDistrictId = 1482;
    int weightGram = (int) Math.round(req.getWeightKg() * 1000);
    String url = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";
    HttpHeaders headers = new HttpHeaders();
    headers.set("Token", token);
    headers.set("ShopId", shopId);
    headers.setContentType(MediaType.APPLICATION_JSON);
    String body =
        String.format(
            "{"
                + "\"service_id\":%d,"
                + "\"insurance_value\":0,"
                + "\"coupon\":null,"
                + "\"from_district_id\":%d,"
                + "\"to_district_id\":%d,"
                + "\"to_ward_code\":\"%s\","
                + "\"height\":%d,"
                + "\"length\":%d,"
                + "\"weight\":%d,"
                + "\"width\":%d"
                + "}",
            serviceId,
            fromDistrictId,
            req.getToDistrictId(),
            req.getToWardCode(),
            req.getHeight(),
            req.getLength(),
            weightGram,
            req.getWidth());
    HttpEntity<String> entity = new HttpEntity<>(body, headers);
    try {
      ResponseEntity<String> response =
          restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
      String respBody = response.getBody();
      com.fasterxml.jackson.databind.ObjectMapper mapper =
          new com.fasterxml.jackson.databind.ObjectMapper();
      com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(respBody);
      int code = node.get("code").asInt();
      if (code != 200) {
        String message = node.has("message") ? node.get("message").asText() : "GHN API error";
        if (message.contains("route not found service")) {
          int manualFee = calculateManualShippingFee(req.getWeightKg(), req.getToProvinceName());
          return new ShippingFeeResponseDTO(manualFee, null);
        }
        return new ShippingFeeResponseDTO(0, message);
      }
      int total = node.get("data").get("total").asInt(0);
      return new ShippingFeeResponseDTO(total, null);
    } catch (Exception ex) {
      String msg = ex.getMessage();
      if (msg != null && msg.contains("route not found service")) {
        int manualFee = calculateManualShippingFee(req.getWeightKg(), req.getToProvinceName());
        return new ShippingFeeResponseDTO(manualFee, null);
      }
      return new ShippingFeeResponseDTO(0, msg);
    }
  }

  private int calculateManualShippingFee(Double weightKg, String provinceName) {
    int base = 25000, step = 2500;
    String name = provinceName != null ? provinceName.toLowerCase() : "";
    if (name.contains("hà nội")) {
      base = 20000;
      step = 2500;
    } else if (name.contains("hồ chí minh") || name.contains("hcm")) {
      base = 30000;
      step = 5000;
    }
    if (weightKg == null || weightKg <= 3) return base;
    int extra = (int) Math.ceil((weightKg - 3) / 0.5) * step;
    return base + extra;
  }
}
