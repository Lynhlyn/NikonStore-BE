package com.example.nikonbe.modules.customer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** DTO cho việc khoá tài khoản khách hàng */
@Data
public class BlockCustomerDTO {

  @NotBlank(message = "Lý do khoá tài khoản không được để trống")
  @Size(min = 5, max = 500, message = "Lý do khoá tài khoản phải từ 5 đến 500 ký tự")
  private String reason;
}
