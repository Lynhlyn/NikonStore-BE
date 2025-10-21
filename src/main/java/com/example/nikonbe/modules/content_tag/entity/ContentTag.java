package com.example.nikonbe.modules.content_tag.entity;

import com.example.nikonbe.common.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "content_tag")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentTag extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Tên tag không được để trống")
  @Column(nullable = false, unique = true)
  private String name;

  @NotBlank(message = "Slug không được để trống")
  @Column(nullable = false, unique = true)
  private String slug;

  @NotBlank(message = "Type không được để trống")
  @Column(nullable = false)
  private String type;
}
