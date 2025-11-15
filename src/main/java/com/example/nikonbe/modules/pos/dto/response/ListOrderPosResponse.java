package com.example.nikonbe.modules.pos.dto.response;

import com.example.nikonbe.modules.customer.dto.response.CustomerResponseDTO;
import com.example.nikonbe.modules.order_detail.dto.response.OrderDetailReponse;
import com.example.nikonbe.modules.staff.dto.response.StaffResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class ListOrderPosResponse {

  @Schema(description = "ID đơn hàng", example = "1")
  private Integer id;

  @Schema(description = "ID khách hàng (nullable)", example = "1")
  private CustomerResponseDTO customer;

  @Schema(description = "Tổng tiền đơn hàng", example = "0")
  private BigDecimal totalAmount;

  @Schema(description = "Giam giá", example = "0")
  private BigDecimal discount;

  @Schema(description = "ID voucher (nullable)", example = "2")
  private Integer voucherId;

  @Schema(description = "Phương thức thanh toán (cash, card...)", example = "cash", required = true)
  private String paymentMethod;

  @Schema(description = "Trạng thái thanh toán", example = "pending", required = true)
  private String paymentStatus;

  @Schema(description = "Ghi chú đơn hàng", example = "Khách chưa thanh toán")
  private String note;

  @Schema(description = "ID nhân viên tạo đơn", example = "3", required = true)
  private StaffResponseDTO staff;

  private List<OrderDetailReponse> orderDetails;
}
