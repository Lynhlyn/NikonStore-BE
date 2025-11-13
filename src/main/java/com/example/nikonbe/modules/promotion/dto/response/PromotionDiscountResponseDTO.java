package com.example.nikonbe.modules.promotion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng phản hồi thông tin giảm giá khuyến mãi")
public class PromotionDiscountResponseDTO {
  @Schema(description = "ID khuyến mãi", example = "1")
  private Integer promotionId;

  @Schema(description = "Tên khuyến mãi", example = "Flash Sale Tết")
  private String name;

  @Schema(description = "Mã khuyến mãi", example = "TET2024")
  private String code;

  @Schema(description = "Số tiền giảm", example = "150000")
  private BigDecimal discountAmount;

  @Schema(description = "Số tiền cuối cùng", example = "350000")
  private BigDecimal finalAmount;

  @Schema(description = "Thông báo", example = "Áp dụng khuyến mãi thành công")
  private String message;

  @Schema(description = "Có thể sử dụng", example = "true")
  private Boolean canUse;
}
