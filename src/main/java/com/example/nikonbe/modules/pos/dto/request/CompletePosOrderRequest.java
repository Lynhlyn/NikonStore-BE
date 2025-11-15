package com.example.nikonbe.modules.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request hoàn tất đơn hàng POS")
public class CompletePosOrderRequest {

  @Schema(description = "Phương thức thanh toán", example = "cash", required = true)
  private String paymentMethod;

  @Schema(description = "Số tiền khách đưa", example = "2000000", required = true)
  private BigDecimal amountPaid;

  @Schema(description = "Tiền thừa trả lại khách", example = "100000")
  private BigDecimal changeAmount;

  @Schema(description = "Id voucher áp dụng cho đơn hàng (nullable)", example = "2")
  private Integer voucherId;

  @Schema(description = "Ghi chú thanh toán", example = "Thanh toán hoàn tất")
  private String paymentNote;

  @Schema(description = "Ghi chú đơn hàng", example = "Đơn hàng hoàn tất")
  private String orderNote;
}
