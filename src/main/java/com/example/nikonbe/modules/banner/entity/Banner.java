package com.example.nikonbe.modules.banner.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.utils.PositionConverter;
import com.example.nikonbe.common.utils.StatusConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@Table(name = "banners")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Banner extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Tên banner không được để trống")
  @Column(nullable = true, unique = true)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @NotBlank(message = "URL không được để trống")
  @Column(nullable = true)
  private String url;

  @NotNull(message = "Trạng thái không được để trống")
  @Convert(converter = StatusConverter.class)
  @Column(nullable = false)
  private Status status;

  @NotBlank(message = "Hình ảnh không được để trống")
  @Column(name = "image", nullable = false)
  private String imageUrl;

  @NotNull(message = "Vị trí không được để trống")
  @Convert(converter = PositionConverter.class)
  @Column(nullable = true, columnDefinition = "JSON")
  private Integer position;

  @Column(name = "display_order")
  private Integer displayOrder;
}
