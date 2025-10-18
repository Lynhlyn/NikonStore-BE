package com.example.nikonbe.modules.voucher.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.common.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "voucher")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Voucher extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Mã voucher không được để trống")
  @Column(name = "code", nullable = false, unique = true, length = 50)
  private String code;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @NotNull(message = "Số lượng voucher không được để trống")
  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @NotBlank(message = "Loại giảm giá không được để trống")
  @Column(name = "discount_type", nullable = false, length = 50)
  private String discountType;

  @NotNull(message = "Giá trị giảm giá không được để trống")
  @Column(name = "discount_value", nullable = false, precision = 15, scale = 2)
  private BigDecimal discountValue;

  @Builder.Default
  @Column(name = "min_order_value", precision = 15, scale = 0)
  private BigDecimal minOrderValue = BigDecimal.ZERO;

  @Column(name = "max_discount", precision = 15, scale = 0)
  private BigDecimal maxDiscount;

  @NotNull(message = "Ngày bắt đầu không được để trống")
  @Column(name = "start_date", nullable = false)
  private LocalDateTime startDate;

  @NotNull(message = "Ngày kết thúc không được để trống")
  @Column(name = "end_date", nullable = false)
  private LocalDateTime endDate;

  @Builder.Default
  @Column(name = "used_count")
  private Integer usedCount = 0;

  @Builder.Default
  @NotNull(message = "Trạng thái không được để trống")
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private Status status = Status.ACTIVE;

  @Builder.Default
  @Column(name = "is_public", nullable = false)
  private Boolean isPublic = true;
}
