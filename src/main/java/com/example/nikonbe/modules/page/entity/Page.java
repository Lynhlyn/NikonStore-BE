package com.example.nikonbe.modules.page.entity;

import com.example.nikonbe.common.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@Table(name = "page")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Page extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Tiêu đề không được để trống")
  @Size(max = 255, message = "Tiêu đề không được quá 255 ký tự")
  @Column(nullable = false, length = 255)
  private String title;

  @NotBlank(message = "Slug không được để trống")
  @Size(max = 255, message = "Slug không được quá 255 ký tự")
  @Column(nullable = false, unique = true, length = 255)
  private String slug;

  @NotBlank(message = "Nội dung không được để trống")
  @Column(nullable = false, columnDefinition = "LONGTEXT")
  private String content;
}
