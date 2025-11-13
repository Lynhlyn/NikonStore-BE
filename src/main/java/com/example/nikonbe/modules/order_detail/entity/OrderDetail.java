package com.example.nikonbe.modules.order_detail.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.modules.orders.entity.Order;
import com.example.nikonbe.modules.product_detail.entity.ProductDetail;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "order_details")
public class OrderDetail extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", foreignKey = @ForeignKey(name = "fk_order_detail_order"))
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "product_detail_id",
      foreignKey = @ForeignKey(name = "fk_order_detail_product_detail"))
  private ProductDetail productDetail;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Column(name = "price", nullable = false)
  private BigDecimal price;

  @Column(name = "discount")
  private BigDecimal discount;
}
