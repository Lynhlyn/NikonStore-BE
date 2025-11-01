package com.example.nikonbe.modules.cart_detail.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.modules.cart.entity.Cart;
import com.example.nikonbe.modules.product_detail.entity.ProductDetail;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "cart_detail")
public class CartDetail extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "cart_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_cart_detail_cart"))
  private Cart cart;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "product_detail_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_cart_detail_product_detail"))
  private ProductDetail productDetail;

  @NotNull(message = "Số lượng không được để trống")
  @Min(value = 1, message = "Số lượng phải lớn hơn 0")
  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @NotNull(message = "Giá không được để trống")
  @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
  @Column(name = "price", nullable = false)
  private BigDecimal price;
}
