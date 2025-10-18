package com.example.nikonbe.modules.voucher.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
@Schema(description = "Đối tượng yêu cầu tạo mới voucher")
public class VoucherCreateDTO {

  @NotBlank(message = "Mã voucher không được để trống")
  @Size(max = 50, message = "Mã voucher không được vượt quá 50 ký tự")
  @Schema(description = "Mã voucher", example = "SUMMER2024", required = true)
  private String code;

  @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
  @Schema(description = "Mô tả voucher", example = "Giảm giá cho mùa hè 2024")
  private String description;

  @NotNull(message = "Số lượng voucher không được để trống")
  @Min(value = 1, message = "Số lượng voucher phải lớn hơn 0")
  @Schema(description = "Số lượng voucher", example = "100", required = true)
  private Integer quantity;

  @NotBlank(message = "Loại giảm giá không được để trống")
  @Pattern(
      regexp = "^(percentage|fixed_amount)$",
      message = "Loại giảm giá phải là 'percentage' hoặc 'fixed_amount'")
  @Schema(
      description = "Loại giảm giá (phần trăm hoặc số tiền cố định)",
      example = "percentage",
      required = true,
      allowableValues = {"percentage", "fixed_amount"})
  private String discountType;

  @NotNull(message = "Giá trị giảm giá không được để trống")
  @DecimalMin(value = "0.01", message = "Giá trị giảm giá phải lớn hơn 0")
  @Schema(description = "Giá trị giảm giá", example = "10.00", required = true)
  private BigDecimal discountValue;

  @Builder.Default
  @DecimalMin(value = "0", message = "Giá trị đơn hàng tối thiểu phải lớn hơn hoặc bằng 0")
  @Schema(description = "Giá trị đơn hàng tối thiểu", example = "100000")
  private BigDecimal minOrderValue = BigDecimal.ZERO;

  @DecimalMin(value = "0", message = "Giá trị giảm tối đa phải lớn hơn 0")
  @Schema(description = "Giá trị giảm tối đa", example = "50000")
  private BigDecimal maxDiscount;

  @NotNull(message = "Ngày bắt đầu không được để trống")
  @Future(message = "Ngày bắt đầu phải là thời gian trong tương lai")
  @Schema(description = "Ngày bắt đầu", example = "2024-06-01T00:00:00", required = true)
  private LocalDateTime startDate;

  @NotNull(message = "Ngày kết thúc không được để trống")
  @Schema(description = "Ngày kết thúc", example = "2024-08-31T23:59:59", required = true)
  private LocalDateTime endDate;

  @Builder.Default
  @Schema(description = "Voucher có công khai không", example = "true", defaultValue = "true")
  private Boolean isPublic = true;

  @AssertTrue(message = "Ngày kết thúc phải sau ngày bắt đầu")
  @Schema(hidden = true)
  public boolean isEndDateAfterStartDate() {
    if (startDate == null || endDate == null) {
      return true;
    }
    return endDate.isAfter(startDate);
  }

  @AssertTrue(message = "Giá trị giảm giá theo phần trăm phải từ 1% đến 100%")
  @Schema(hidden = true)
  public boolean isValidPercentageDiscount() {
    if (!"percentage".equals(discountType) || discountValue == null) {
      return true;
    }
    return discountValue.compareTo(BigDecimal.ONE) >= 0
        && discountValue.compareTo(BigDecimal.valueOf(100)) <= 0;
  }
}
