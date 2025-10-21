package com.example.nikonbe.modules.contact.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.utils.StatusConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "contact")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contact extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Tên liên hệ không được để trống")
  @Column(nullable = false)
  private String name;

  @NotBlank(message = "Phone không được để trống")
  @Column(nullable = false)
  private String phone;

  @NotBlank(message = "Nội dung không được để trống")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @NotNull(message = "Trạng thái không được để trống")
  @Convert(converter = StatusConverter.class)
  @Column(nullable = false)
  private Status status;
}
