package com.example.nikonbe.modules.pos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request cập nhật đơn hàng POS pending")
public class UpdatePosPendingOrderRequest {

  @Schema(description = "ID khách hàng (nullable)", example = "1")
  private Integer customerId;

  @Schema(description = "ID voucher (nullable)", example = "2")
  private Integer voucherId;

  @Schema(description = "Phương thức thanh toán", example = "cash")
  private String paymentMethod;

  @Schema(description = "Trạng thái thanh toán", example = "pending")
  private String paymentStatus;

  @Schema(description = "Ghi chú đơn hàng", example = "Ghi chú cập nhật")
  private String note;

  @Schema(description = "Danh sách chi tiết sản phẩm trong đơn hàng")
  private List<OrderDetailItem> orderDetails;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @Schema(description = "Chi tiết sản phẩm trong đơn hàng")
  public static class OrderDetailItem {

    @Schema(description = "ID chi tiết sản phẩm", example = "1", required = true)
    private Integer productDetailId;

    @Schema(description = "Số lượng", example = "2", required = true)
    private Integer quantity;

    @Schema(description = "Ghi chú cho sản phẩm này", example = "Ghi chú đặc biệt")
    private String note;
  }
}
