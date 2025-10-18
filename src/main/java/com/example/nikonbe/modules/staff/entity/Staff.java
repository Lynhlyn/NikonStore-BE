package com.example.nikonbe.modules.staff.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "staff")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Staff extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Username không được để trống")
  @Column(nullable = false, unique = true)
  private String username;

  @NotBlank(message = "Password không được để trống")
  @Column(nullable = false)
  private String password;

  @NotBlank(message = "Họ tên không được để trống")
  @Column(name = "full_name", nullable = false)
  private String fullName;

  @NotBlank(message = "Số điện thoại không được để trống")
  @Column(name = "phone_number", nullable = false)
  private String phoneNumber;

  @NotBlank(message = "Email không được để trống")
  @Email(message = "Email không đúng định dạng")
  @Column(nullable = false, unique = true)
  private String email;

  @NotNull(message = "Vai trò không được để trống")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole role;

  @NotNull(message = "Trạng thái không được để trống")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;
}
