package com.example.nikonbe.modules.product_tag.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductTagUpdateDTO {

  @NotNull(message = "Tag IDs are required")
  @Size(min = 1, message = "At least one tag ID is required")
  private List<Integer> tagIds;
}
