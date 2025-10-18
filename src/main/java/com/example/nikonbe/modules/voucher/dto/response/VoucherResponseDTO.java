package com.example.nikonbe.modules.voucher.dto.response;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Thông tin phản hồi của voucher")
public class VoucherResponseDTO {

  @Schema(description = "ID của voucher", example = "1")
  private String id;

  @Schema(description = "Mã voucher", example = "SUMMER2024")
  private String code;

  @Schema(description = "Mô tả voucher", example = "Giảm giá cho mùa hè 2024")
  private String description;

  @Schema(description = "Số lượng voucher", example = "100")
  private Integer quantity;

  @Schema(description = "Loại giảm giá (phần trăm hoặc số tiền cố định)", example = "percentage")
  private String discountType;

  @Schema(description = "Giá trị giảm giá", example = "10.00")
  private BigDecimal discountValue;

  @Schema(description = "Giá trị đơn hàng tối thiểu", example = "100000")
  private BigDecimal minOrderValue;

  @Schema(description = "Giá trị giảm tối đa", example = "50000")
  private BigDecimal maxDiscount;

  @Schema(description = "Ngày bắt đầu", example = "2024-06-01T00:00:00")
  private LocalDateTime startDate;

  @Schema(description = "Ngày kết thúc", example = "2024-08-31T23:59:59")
  private LocalDateTime endDate;

  @Schema(description = "Số lần đã sử dụng", example = "0")
  private Integer usedCount;

  @Schema(description = "Trạng thái voucher", example = "ACTIVE")
  private Status status;

  @Schema(description = "Voucher có công khai không", example = "true")
  private Boolean isPublic;

  @Schema(description = "Thời gian tạo", example = "2024-05-15T10:30:00")
  private LocalDateTime createdAt;

  @Schema(description = "Thời gian cập nhật", example = "2024-05-15T10:30:00")
  private LocalDateTime updatedAt;
}
