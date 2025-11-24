package com.example.nikonbe.security.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingFeeRequestDTO {
  private Integer toDistrictId;
  private String toWardCode;
  private String toProvinceName;
  private Double weightKg;
  private Integer length;
  private Integer width;
  private Integer height;
}
