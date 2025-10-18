package com.example.nikonbe.modules.voucher.dto.response;

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
@Schema(description = "Thông tin phản hồi khi áp dụng voucher")
public class VoucherDiscountResponseDTO {

  @Schema(description = "Mã voucher", example = "SUMMER2024")
  private String code;

  @Schema(description = "Số tiền giảm giá", example = "50000")
  private BigDecimal discountAmount;

  @Schema(description = "Số tiền cuối cùng sau khi giảm", example = "450000")
  private BigDecimal finalAmount;

  @Schema(description = "Có thể sử dụng voucher không", example = "true")
  private Boolean canUse;

  @Schema(description = "Thông báo", example = "Áp dụng voucher thành công")
  private String message;
}
