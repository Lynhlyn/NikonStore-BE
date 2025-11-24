package com.example.nikonbe.security.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShippingFeeResponseDTO {
  private int shippingFee;
  private String error;

  public ShippingFeeResponseDTO(int shippingFee, String error) {
    this.shippingFee = shippingFee;
    this.error = error;
  }
}
