package com.example.nikonbe.modules.promotion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO tạo mới khuyến mãi")
public class PromotionCreateDTO {

  @NotBlank
  @Size(max = 255)
  @Schema(description = "Tên khuyến mãi", example = "Flash Sale Tết", required = true)
  private String name;

  @NotBlank
  @Size(max = 255)
  @Schema(description = "Tiêu đề khuyến mãi", example = "Giảm giá 30%", required = true)
  private String title;

  @Size(max = 50)
  @Schema(description = "Mã khuyến mãi", example = "TET2024")
  private String code;

  @NotBlank
  @Pattern(regexp = "^(percentage|fixed_amount)$")
  @Schema(description = "Loại giảm giá", example = "percentage", required = true)
  private String discountType;

  @NotNull
  @DecimalMin(value = "0.01")
  @Schema(description = "Giá trị giảm giá", example = "30.0", required = true)
  private BigDecimal discountValue;

  @NotNull
  @Future
  @Schema(description = "Ngày bắt đầu", example = "2024-01-20T00:00:00", required = true)
  private LocalDateTime startDate;

  @NotNull
  @Schema(description = "Ngày kết thúc", example = "2024-01-25T23:59:59", required = true)
  private LocalDateTime endDate;

  @Size(max = 1000)
  @Schema(description = "Mô tả khuyến mãi", example = "Chương trình khuyến mãi dịp Tết")
  private String description;

  @Schema(description = "Danh sách ID product details", example = "[1, 2, 3]")
  private List<Integer> productDetailIds;

  @AssertTrue(message = "Ngày kết thúc phải sau ngày bắt đầu")
  public boolean isEndDateAfterStartDate() {
    return startDate == null || endDate == null || endDate.isAfter(startDate);
  }

  @AssertTrue(message = "Giảm giá % phải từ 1 đến 100")
  public boolean isValidPercentageDiscount() {
    return !"percentage".equals(discountType)
        || discountValue == null
        || (discountValue.compareTo(BigDecimal.ONE) >= 0
            && discountValue.compareTo(BigDecimal.valueOf(100)) <= 0);
  }
}
