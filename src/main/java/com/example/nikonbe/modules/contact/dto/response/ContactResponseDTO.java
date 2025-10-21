package com.example.nikonbe.modules.contact.dto.response;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin phản hồi của liên hệ")
public class ContactResponseDTO {
  @Schema(description = "ID liên hệ", example = "1")
  private Integer id;

  @Schema(description = "Tên liên hệ", example = "Nguyễn Văn A")
  private String name;

  @Schema(description = "Phone liên hệ", example = "0987654321")
  private String phone;

  @Schema(description = "Nội dung liên hệ", example = "Tôi cần hỗ trợ...")
  private String content;

  @Schema(description = "Trạng thái liên hệ", example = "ACTIVE")
  private Status status;

  @Schema(description = "Thời gian tạo", example = "2025-06-16T10:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "Thời gian cập nhật", example = "2025-06-16T11:00:00")
  private LocalDateTime updatedAt;
}
