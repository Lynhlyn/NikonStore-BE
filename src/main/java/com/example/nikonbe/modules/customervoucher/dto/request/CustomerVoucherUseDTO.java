package com.example.nikonbe.modules.customervoucher.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu sử dụng voucher của khách hàng")
public class CustomerVoucherUseDTO {
  @NotNull(message = "Customer ID không được để trống")
  @Schema(description = "ID khách hàng", example = "1", required = true)
  private Integer customerId;

  @NotNull(message = "Voucher ID không được để trống")
  @Schema(description = "ID voucher", example = "1", required = true)
  private Long voucherId;
}
