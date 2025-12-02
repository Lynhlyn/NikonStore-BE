package com.example.nikonbe.modules.review.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.order_detail.entity.OrderDetail;
import com.example.nikonbe.modules.product.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "review")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "product_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_review_product"))
  private Product product;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "customer_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_review_customer"))
  private Customer customer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_detail_id", foreignKey = @ForeignKey(name = "fk_review_order_detail"))
  private OrderDetail orderDetail;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "product_detail_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_review_product_detail"))
  private com.example.nikonbe.modules.product_detail.entity.ProductDetail productDetail;

  @NotNull
  @Min(1)
  @Max(5)
  @Column(nullable = false)
  private Integer rating;

  @Column(columnDefinition = "TEXT")
  private String comment;

  @NotNull
  @Column(nullable = false)
  @Builder.Default
  private Integer status = 1;

  @OneToMany(
      mappedBy = "review",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<ReviewImage> reviewImages;
}
