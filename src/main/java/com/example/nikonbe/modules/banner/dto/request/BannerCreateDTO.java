package com.example.nikonbe.modules.banner.dto.request;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO tạo mới banner")
public class BannerCreateDTO {

  @NotBlank(message = "Tên banner không được để trống")
  @Schema(description = "Tên banner", example = "Banner Khuyến Mãi Nikon", required = true)
  private String name;

  @Schema(description = "Mô tả banner", example = "Khuyến mãi lớn nhất trong năm cho máy ảnh Nikon")
  private String description;

  @NotBlank(message = "URL không được để trống")
  @Schema(
      description = "URL liên kết của banner",
      example = "https://nikonstore.com/promotion",
      required = true)
  private String url;

  @NotNull(message = "Trạng thái không được để trống")
  @Schema(description = "Trạng thái của banner", example = "ACTIVE", required = true)
  private Status status;

  @NotBlank(message = "URL hình ảnh không được để trống")
  @Schema(
      description = "URL hình ảnh của banner",
      example = "https://nikonstore.com/images/banner-promotion.jpg",
      required = true)
  private String imageUrl;

  @NotBlank(message = "Vị trí không được để trống")
  @Schema(
      description = "Vị trí hiển thị của banner",
      example = "homepage-carousel",
      required = true)
  private String position;

  @PositiveOrZero(message = "Thứ tự hiển thị phải là số dương hoặc 0")
  @Schema(description = "Thứ tự hiển thị", example = "1")
  private Integer displayOrder;

  @Schema(description = "Trạng thái hoạt động", example = "true")
  @Builder.Default
  private Boolean isActive = true;
}
