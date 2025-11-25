package com.example.nikonbe.modules.shipping_address.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu tạo mới địa chỉ giao hàng")
public class CreateShippingAddressDTO {

  @NotNull(message = "ID khách hàng không được để trống")
  @Positive(message = "ID khách hàng phải là số dương")
  @Schema(description = "ID tài khoản khách hàng", example = "1", required = true)
  private Integer customerId;

  @NotBlank(message = "Tên người nhận không được để trống")
  @Size(min = 2, max = 100, message = "Tên người nhận phải có từ 2-100 ký tự")
  @Schema(description = "Tên người nhận", example = "Nguyễn Văn An", required = true)
  private String recipientName;

  @NotBlank(message = "Số điện thoại không được để trống")
  @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0")
  @Schema(description = "Số điện thoại người nhận", example = "0123456789", required = true)
  private String recipientPhoneNumber;

  @NotBlank(message = "Tỉnh/Thành phố không được để trống")
  @Size(min = 2, max = 100, message = "Tên tỉnh/thành phố phải có từ 2-100 ký tự")
  @Schema(description = "Tỉnh/Thành phố", example = "TP. Hồ Chí Minh", required = true)
  private String province;

  @NotBlank(message = "Quận/Huyện không được để trống")
  @Size(min = 2, max = 100, message = "Tên quận/huyện phải có từ 2-100 ký tự")
  @Schema(description = "Quận/Huyện", example = "Quận 1", required = true)
  private String district;

  @NotBlank(message = "Phường/Xã không được để trống")
  @Size(min = 2, max = 100, message = "Tên phường/xã phải có từ 2-100 ký tự")
  @Schema(description = "Phường/Xã", example = "Phường Bến Nghé", required = true)
  private String ward;

  @NotBlank(message = "Địa chỉ chi tiết không được để trống")
  @Size(min = 5, max = 255, message = "Địa chỉ chi tiết phải có từ 5-255 ký tự")
  @Schema(description = "Địa chỉ chi tiết", example = "Số 123, Đường Nguyễn Huệ", required = true)
  private String detailedAddress;

  @Builder.Default
  @Schema(description = "Đặt làm địa chỉ mặc định", example = "false")
  private Boolean isDefault = false;
}
