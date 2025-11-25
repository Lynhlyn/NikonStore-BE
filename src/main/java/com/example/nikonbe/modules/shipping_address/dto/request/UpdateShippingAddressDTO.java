package com.example.nikonbe.modules.shipping_address.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu cập nhật địa chỉ giao hàng")
public class UpdateShippingAddressDTO {

  @Size(min = 2, max = 100, message = "Tên người nhận phải có từ 2-100 ký tự")
  @Schema(description = "Tên người nhận", example = "Nguyễn Văn An")
  private String recipientName;

  @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0")
  @Schema(description = "Số điện thoại người nhận", example = "0123456789")
  private String recipientPhoneNumber;

  @Size(min = 2, max = 100, message = "Tên tỉnh/thành phố phải có từ 2-100 ký tự")
  @Schema(description = "Tỉnh/Thành phố", example = "TP. Hồ Chí Minh")
  private String province;

  @Size(min = 2, max = 100, message = "Tên quận/huyện phải có từ 2-100 ký tự")
  @Schema(description = "Quận/Huyện", example = "Quận 1")
  private String district;

  @Size(min = 2, max = 100, message = "Tên phường/xã phải có từ 2-100 ký tự")
  @Schema(description = "Phường/Xã", example = "Phường Bến Nghé")
  private String ward;

  @Size(min = 5, max = 255, message = "Địa chỉ chi tiết phải có từ 5-255 ký tự")
  @Schema(description = "Địa chỉ chi tiết", example = "Số 123, Đường Nguyễn Huệ")
  private String detailedAddress;

  @Schema(description = "Đặt làm địa chỉ mặc định", example = "false")
  private Boolean isDefault;
}
