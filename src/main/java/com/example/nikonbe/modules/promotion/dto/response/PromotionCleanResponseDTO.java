package com.example.nikonbe.modules.promotion.dto.response;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin khuyến mãi clean cho client")
public class PromotionCleanResponseDTO {

  @Schema(description = "ID khuyến mãi", example = "1")
  private Integer id;

  @Schema(description = "Tên khuyến mãi", example = "Flash Sale Tết")
  private String name;

  @Schema(description = "Tiêu đề khuyến mãi", example = "Giảm giá 30%")
  private String title;

  @Schema(description = "Mã khuyến mãi", example = "TET2024")
  private String code;

  @Schema(description = "Loại giảm giá", example = "percentage")
  private String discountType;

  @Schema(description = "Giá trị giảm giá", example = "30.0")
  private BigDecimal discountValue;

  @Schema(description = "Mô tả khuyến mãi", example = "Chương trình khuyến mãi dịp Tết")
  private String description;

  @Schema(description = "Trạng thái", example = "ACTIVE")
  private Status status;

  @Schema(description = "Ngày bắt đầu", example = "2024-01-20T00:00:00")
  private LocalDateTime startDate;

  @Schema(description = "Ngày kết thúc", example = "2024-01-25T23:59:59")
  private LocalDateTime endDate;

  @Schema(description = "Văn bản loại giảm giá", example = "Phần trăm")
  public String getDiscountTypeText() {
    return "percentage".equals(discountType) ? "Phần trăm" : "Số tiền cố định";
  }

  @Schema(description = "Còn hiệu lực", example = "true")
  public Boolean getIsActive() {
    return status == Status.ACTIVE;
  }

  @Schema(description = "Đã hết hạn", example = "false")
  public Boolean getIsExpired() {
    return endDate != null && LocalDateTime.now().isAfter(endDate);
  }

  @Schema(description = "Có thể sử dụng", example = "true")
  public Boolean getCanUse() {
    return getIsActive() && !getIsExpired();
  }
}
