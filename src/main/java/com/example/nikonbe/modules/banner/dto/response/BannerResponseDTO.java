package com.example.nikonbe.modules.banner.dto.response;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO phản hồi thông tin banner")
public class BannerResponseDTO {
  
  @Schema(description = "ID của banner")
  private Long id;
  
  @Schema(description = "Tên banner")
  private String name;
  
  @Schema(description = "Mô tả banner")
  private String description;
  
  @Schema(description = "URL liên kết")
  private String url;
  
  @Schema(description = "Trạng thái")
  private Status status;
  
  @Schema(description = "URL hình ảnh")
  private String imageUrl;
  
  @Schema(description = "Vị trí hiển thị")
  private String position;
  
  @Schema(description = "Thứ tự hiển thị")
  private Integer displayOrder;
  
  @Schema(description = "Trạng thái hoạt động")
  private Boolean isActive;
  
  @Schema(description = "Thời gian tạo")
  private LocalDateTime createdAt;
  
  @Schema(description = "Thời gian cập nhật")
  private LocalDateTime updatedAt;
}
