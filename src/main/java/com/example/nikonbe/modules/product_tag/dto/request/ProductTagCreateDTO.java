package com.example.nikonbe.modules.product_tag.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductTagCreateDTO {

  @NotNull(message = "Tag ID is required")
  private Integer tagId;
}
