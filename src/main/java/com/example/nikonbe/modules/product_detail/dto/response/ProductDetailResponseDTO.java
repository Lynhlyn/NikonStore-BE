package com.example.nikonbe.modules.product_detail.dto.response;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.attributes.capacity.dto.response.CapacityResponseDTO;
import com.example.nikonbe.modules.attributes.color.dto.response.ColorResponseDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponseDTO {
  private Integer id;
  private String sku;
  private Integer stock;
  private Integer reservedStock;
  private Integer productId;
  private String productName;
  private Integer colorId;
  private String colorName;
  private ColorResponseDTO color;
  private Integer capacityId;
  private String capacityName;
  private CapacityResponseDTO capacity;
  private BigDecimal price;
  private Status status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
