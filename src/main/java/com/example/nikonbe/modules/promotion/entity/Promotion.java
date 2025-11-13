package com.example.nikonbe.modules.promotion.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.utils.StatusConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "promotion")
@AllArgsConstructor
@NoArgsConstructor
public class Promotion extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Tên khuyến mãi không được để trống")
  @Column(nullable = false)
  private String name;

  @NotBlank(message = "Tiêu đề không được để trống")
  @Column(nullable = false)
  private String title;

  @Column(length = 50, unique = true)
  private String code;

  @NotBlank(message = "Loại giảm giá không được để trống")
  @Column(name = "discount_type", nullable = false, length = 50)
  private String discountType;

  @NotNull(message = "Giá trị giảm giá không được để trống")
  @Column(name = "discount_value", nullable = false, precision = 15, scale = 2)
  private BigDecimal discountValue;

  @Column(name = "applies_to", length = 50)
  private String appliesTo = "product";

  @Column(name = "applied_product", length = 50)
  private String appliedProduct;

  @NotNull(message = "Ngày bắt đầu không được để trống")
  @Column(name = "start_date", nullable = false)
  private LocalDateTime startDate;

  @NotNull(message = "Ngày kết thúc không được để trống")
  @Column(name = "end_date", nullable = false)
  private LocalDateTime endDate;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @NotNull(message = "Trạng thái không được để trống")
  @Convert(converter = StatusConverter.class)
  @Column(nullable = false)
  private Status status = Status.ACTIVE;
}
