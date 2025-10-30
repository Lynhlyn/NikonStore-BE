package com.example.nikonbe.modules.content_category.entity;

import com.example.nikonbe.common.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "content_category")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentCategory extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Tên danh mục không được để trống")
  @Column(nullable = false, unique = true)
  private String name;

  @NotBlank(message = "Slug không được để trống")
  @Column(nullable = false, unique = true)
  private String slug;

  @Column(columnDefinition = "TEXT")
  private String description;

  @NotBlank(message = "Type không được để trống")
  @Column(nullable = false)
  private String type;
}
