package com.example.nikonbe.modules.promotion.dto.request;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
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
@Schema(description = "DTO cập nhật khuyến mãi")
public class PromotionUpdateDTO {

  @Size(max = 50)
  @Schema(description = "Mã khuyến mãi", example = "TET2024")
  private String code;

  @Size(max = 255)
  @Schema(description = "Tên khuyến mãi", example = "Flash Sale Tết - Updated")
  private String name;

  @Size(max = 255)
  @Schema(description = "Tiêu đề khuyến mãi", example = "Giảm giá 50%")
  private String title;

  @Pattern(regexp = "^(percentage|fixed_amount)$")
  @Schema(description = "Loại giảm giá", example = "percentage")
  private String discountType;

  @DecimalMin(value = "0.01")
  @Schema(description = "Giá trị giảm giá", example = "50.0")
  private BigDecimal discountValue;

  @Schema(description = "Ngày bắt đầu", example = "2024-02-01T00:00:00")
  private LocalDateTime startDate;

  @Schema(description = "Ngày kết thúc", example = "2024-02-05T23:59:59")
  private LocalDateTime endDate;

  @Size(max = 1000)
  @Schema(description = "Mô tả khuyến mãi", example = "Chương trình khuyến mãi đầu năm")
  private String description;

  @Schema(
      description = "Trạng thái khuyến mãi",
      example = "ACTIVE",
      allowableValues = {"ACTIVE", "INACTIVE", "DELETED"})
  private Status status;

  @Schema(description = "Danh sách ID product details (null = không đổi)", example = "[1, 2, 3]")
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
