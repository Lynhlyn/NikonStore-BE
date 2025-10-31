package com.example.nikonbe.modules.customervoucher.dto.response;

import com.example.nikonbe.modules.voucher.dto.response.VoucherResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin phản hồi của voucher của khách hàng")
public class CustomerVoucherResponseDTO {
  @Schema(description = "ID khách hàng", example = "1")
  private Integer customerId;

  @Schema(description = "Tên khách hàng", example = "Nguyễn Văn A")
  private String customerName;

  @Schema(description = "Thông tin voucher")
  private VoucherResponseDTO voucher;

  @Schema(description = "Thời gian sử dụng", example = "2024-05-15T10:30:00")
  private LocalDateTime usedAt;

  @Schema(description = "Trạng thái sử dụng", example = "true")
  private Boolean used;
}
