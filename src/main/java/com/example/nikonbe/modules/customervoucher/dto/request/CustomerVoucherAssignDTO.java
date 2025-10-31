package com.example.nikonbe.modules.customervoucher.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu gán voucher cho khách hàng")
public class CustomerVoucherAssignDTO {
  @NotNull(message = "Customer ID không được để trống")
  @Schema(description = "ID khách hàng", example = "1", required = true)
  private Integer customerId;

  @NotEmpty(message = "Danh sách voucher ID không được để trống")
  @Schema(description = "Danh sách ID voucher cần gán", example = "[1, 2, 3]", required = true)
  private List<Long> voucherIds;
}
