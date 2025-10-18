package com.example.nikonbe.modules.customer.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.common.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "customer")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Username is required")
  @Size(max = 100, message = "Username must not exceed 100 characters")
  @Column(name = "username", nullable = false, unique = true, length = 100)
  private String username;

  @Size(max = 255, message = "Password must not exceed 255 characters")
  @Column(name = "password")
  private String password;

  @NotBlank(message = "Email is required")
  @Email(message = "Email format is invalid")
  @Size(max = 255, message = "Email must not exceed 255 characters")
  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @NotBlank(message = "Full name is required")
  @Size(max = 255, message = "Full name must not exceed 255 characters")
  @Column(name = "full_name", nullable = false)
  private String fullName;

  @NotBlank(message = "Phone number is required")
  @Size(max = 20, message = "Phone number must not exceed 20 characters")
  @Column(name = "phone_number", nullable = false, length = 20)
  private String phoneNumber;

  @Size(max = 500, message = "Image URL must not exceed 500 characters")
  @Column(name = "url_image")
  private String urlImage;

  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  @Size(max = 10, message = "Gender must not exceed 10 characters")
  @Column(name = "gender", length = 10)
  private String gender;

  @Builder.Default
  @Column(name = "is_guest")
  private Boolean isGuest = false;

  @Size(max = 50, message = "Provider must not exceed 50 characters")
  @Column(name = "provider", length = 50)
  private String provider;

  @Size(max = 255, message = "Provider ID must not exceed 255 characters")
  @Column(name = "provider_id")
  private String providerId;

  @NotNull(message = "Status is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private Status status;
}
