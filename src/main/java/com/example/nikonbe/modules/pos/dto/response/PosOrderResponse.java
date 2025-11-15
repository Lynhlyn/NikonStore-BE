package com.example.nikonbe.modules.pos.dto.response;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.customer.dto.response.CustomerResponseDTO;
import com.example.nikonbe.modules.staff.dto.response.StaffResponseDTO;
import com.example.nikonbe.modules.voucher.dto.response.VoucherResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Thông tin chi tiết đơn hàng POS")
public class PosOrderResponse {

  @Schema(description = "ID đơn hàng", example = "1")
  private Integer id;

  @Schema(description = "Mã đơn hàng", example = "ORD-20241201-001")
  private String code;

  @Schema(description = "Thông tin khách hàng")
  private CustomerResponseDTO customer;

  @Schema(description = "Thông tin nhân viên")
  private StaffResponseDTO staff;

  @Schema(description = "Tổng tiền sản phẩm", example = "2000000")
  private BigDecimal subtotal;

  @Schema(description = "Giảm giá sản phẩm", example = "200000")
  private BigDecimal productDiscount;

  @Schema(description = "Thông tin voucher")
  private VoucherResponseDTO voucher;

  @Schema(description = "Giảm giá từ voucher", example = "100000")
  private BigDecimal voucherDiscount;

  @Schema(description = "Tổng giảm giá", example = "300000")
  private BigDecimal totalDiscount;

  @Schema(description = "Tổng tiền cuối cùng", example = "1700000")
  private BigDecimal totalAmount;

  @Schema(description = "Phương thức thanh toán", example = "cash")
  private String paymentMethod;

  @Schema(description = "Trạng thái thanh toán", example = "completed")
  private String paymentStatus;

  @Schema(description = "Trạng thái đơn hàng", example = "COMPLETED")
  private Status status;

  @Schema(description = "Ghi chú", example = "Ghi chú đơn hàng")
  private String note;

  @Schema(description = "Ngày tạo", example = "2024-12-01T10:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "Ngày cập nhật", example = "2024-12-01T11:00:00")
  private LocalDateTime updatedAt;

  @Schema(description = "Danh sách chi tiết sản phẩm")
  private List<PosOrderDetailResponse> orderDetails;
}
