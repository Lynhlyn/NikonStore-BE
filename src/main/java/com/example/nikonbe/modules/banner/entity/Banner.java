package com.example.nikonbe.modules.banner.entity;

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
@Table(name = "banners")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Banner extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Tên banner không được để trống")
  @Column(nullable = false, unique = true)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @NotBlank(message = "URL không được để trống")
  @Column(nullable = false)
  private String url;

  @NotNull(message = "Trạng thái không được để trống")
  @Convert(converter = StatusConverter.class)
  @Column(nullable = false)
  private Status status;

  @NotBlank(message = "Hình ảnh không được để trống")
  @Column(nullable = false)
  private String imageUrl;

  @NotBlank(message = "Vị trí không được để trống")
  @Column(nullable = false)
  private String position;

  @Column(name = "display_order")
  private Integer displayOrder;

  @Column(name = "is_active")
  @Builder.Default
  private Boolean isActive = true;
}
