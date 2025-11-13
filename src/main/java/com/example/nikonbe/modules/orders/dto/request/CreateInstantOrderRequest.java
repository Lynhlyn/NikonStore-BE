package com.example.nikonbe.modules.orders.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateInstantOrderRequest {
  private Integer customerId;
  private String cookieId;

  @NotEmpty(message = "Danh sách sản phẩm không được để trống")
  @Valid
  private List<InstantOrderItemRequest> items;

  @NotBlank(message = "Địa chỉ giao hàng không được để trống")
  private String shippingAddress;

  @NotBlank(message = "Phương thức thanh toán không được để trống")
  private String paymentMethod;

  private Long voucherId;
  private BigDecimal discount;
  private String notes;

  @NotBlank(message = "Tên người nhận không được để trống")
  private String recipientName;

  @NotBlank(message = "Số điện thoại người nhận không được để trống")
  private String recipientPhone;

  @NotBlank(message = "Email người nhận không được để trống")
  private String recipientEmail;

  private BigDecimal shippingFee;
  private String orderType;
  private String ipAddress;

  @Data
  public static class InstantOrderItemRequest {
    @NotNull(message = "ID sản phẩm không được để trống")
    private Integer productDetailId;

    @NotNull(message = "Số lượng không được để trống")
    private Integer quantity;
  }
}
