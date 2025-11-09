package com.example.nikonbe.modules.customer.dto.request;

import com.example.nikonbe.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng yêu cầu tạo mới khách hàng")
public class CreateCustomerDTO {

  private String username;
  private String email;
  private String fullName;
  private String phoneNumber;
  private LocalDate dateOfBirth;
  @Builder.Default
  private String gender = "null";
  @Builder.Default
  private Boolean isGuest = false;
  private Status status;
}

