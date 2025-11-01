package com.example.nikonbe.modules.cart.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCartRequest {
  private Integer customerId;
  private String cookieId;
}
