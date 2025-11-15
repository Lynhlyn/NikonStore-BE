package com.example.nikonbe.modules.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description =
        "Request tạo đơn hàng POS (offline) với trạng thái PENDING_PAYMENT và không có OrderDetail")
public class CreatePosPendingOrderRequest {

  @Schema(description = "ID khách hàng (nullable)", example = "1")
  private Integer customerId;

  @Schema(description = "Tổng tiền đơn hàng", example = "0")
  private BigDecimal totalAmount;

  @Schema(description = "ID voucher (nullable)", example = "2")
  private Integer voucherId;

  @Schema(description = "Phương thức thanh toán (cash, card...)", example = "cash", required = true)
  private String paymentMethod;

  @Schema(description = "Trạng thái thanh toán", example = "pending", required = true)
  private String paymentStatus;

  @Schema(description = "Ghi chú đơn hàng", example = "Khách chưa thanh toán")
  private String note;

  @Schema(description = "ID nhân viên tạo đơn", example = "3", required = true)
  private Integer staffId;
}
