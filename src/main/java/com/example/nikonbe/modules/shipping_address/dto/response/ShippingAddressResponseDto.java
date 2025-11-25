package com.example.nikonbe.modules.shipping_address.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin phản hồi chi tiết của địa chỉ giao hàng")
public class ShippingAddressResponseDto {

  @Schema(description = "ID địa chỉ", example = "1")
  private Integer id;

  @Schema(description = "ID khách hàng", example = "1")
  private Integer customerId;

  @Schema(description = "Tên người nhận", example = "Nguyễn Văn An")
  private String recipientName;

  @Schema(description = "Số điện thoại người nhận", example = "0123456789")
  private String recipientPhoneNumber;

  @Schema(description = "Tỉnh/Thành phố", example = "TP. Hồ Chí Minh")
  private String province;

  @Schema(description = "Quận/Huyện", example = "Quận 1")
  private String district;

  @Schema(description = "Phường/Xã", example = "Phường Bến Nghé")
  private String ward;

  @Schema(description = "Địa chỉ chi tiết", example = "Số 123, Đường Nguyễn Huệ")
  private String detailedAddress;

  @Schema(
      description = "Địa chỉ đầy đủ (được format)",
      example = "Số 123, Đường Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh")
  private String fullAddress;

  @Schema(description = "Là địa chỉ mặc định", example = "true")
  private Boolean isDefault;

  @Schema(description = "Thời gian tạo", example = "2024-01-15T10:30:00")
  private LocalDateTime createdAt;

  @Schema(description = "Thời gian cập nhật cuối", example = "2024-01-15T14:30:00")
  private LocalDateTime updatedAt;
}
