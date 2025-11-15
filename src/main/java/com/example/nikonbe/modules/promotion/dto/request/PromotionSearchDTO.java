package com.example.nikonbe.modules.promotion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng tìm kiếm khuyến mãi")
public class PromotionSearchDTO {
  @Schema(description = "Tên khuyến mãi", example = "Flash Sale")
  private String name;

  @Schema(description = "Mã khuyến mãi", example = "TET2024")
  private String code;

  @Schema(description = "Trạng thái (1: hoạt động, 0: không hoạt động)", example = "1")
  private Integer status;

  @Schema(description = "Loại giảm giá", example = "percentage")
  private String discountType;

  @Schema(description = "Đối tượng áp dụng", example = "all")
  private String appliesTo;

  @Builder.Default
  @Schema(description = "Sắp xếp theo trường", example = "id")
  private String sortBy = "id";

  @Builder.Default
  @Schema(description = "Thứ tự sắp xếp", example = "desc")
  private String sortDir = "desc";

  @Builder.Default
  @Schema(description = "Số trang", example = "0")
  private Integer page = 0;

  @Builder.Default
  @Schema(description = "Kích thước trang", example = "10")
  private Integer size = 10;
}
