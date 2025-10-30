package com.example.nikonbe.modules.product_detail.dto.request;

import com.example.nikonbe.common.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailCreateDTO {

  @NotBlank private String sku;

  @NotNull private Integer stock;

  private Integer reservedStock;

  @NotNull private Integer productId;

  private Integer colorId;

  private Integer capacityId;

  @NotNull private BigDecimal price;

  @NotNull private Status status;
}
