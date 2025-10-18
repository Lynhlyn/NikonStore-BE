package com.example.nikonbe.modules.staff.dto.response;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin phản hồi staff")
public class StaffResponseDTO {
  @Schema(description = "ID staff", example = "1")
  private Integer id;

  @Schema(description = "Username", example = "admin")
  private String username;

  @Schema(description = "Họ tên", example = "Nguyễn Văn A")
  private String fullName;

  @Schema(description = "Số điện thoại", example = "0987654321")
  private String phoneNumber;

  @Schema(description = "Email", example = "staff@example.com")
  private String email;

  @Schema(description = "Vai trò", example = "ADMIN")
  private String role;

  @Schema(description = "Trạng thái", example = "ACTIVE")
  private Status status;

  @Schema(description = "Thời gian tạo", example = "2025-06-16T10:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "Thời gian cập nhật", example = "2025-06-16T11:00:00")
  private LocalDateTime updatedAt;
}
