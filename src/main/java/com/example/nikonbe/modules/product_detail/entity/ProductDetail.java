package com.example.nikonbe.modules.product_detail.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.utils.StatusConverter;
import com.example.nikonbe.modules.attributes.capacity.entity.Capacity;
import com.example.nikonbe.modules.attributes.color.entity.Color;
import com.example.nikonbe.modules.product.entity.Product;
import com.example.nikonbe.modules.promotion.entity.Promotion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "product_detail")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetail extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 50)
  private String sku;

  @Column(nullable = false)
  private Integer stock;

  @Column private Integer reservedStock;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "fk_pd_product"))
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "color_id", foreignKey = @ForeignKey(name = "fk_pd_color"))
  private Color color;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "capacity_id", foreignKey = @ForeignKey(name = "fk_pd_capacity"))
  private Capacity capacity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "promotion_id", foreignKey = @ForeignKey(name = "fk_product_detail_promotion"))
  private Promotion promotion;

  @Column(nullable = false, precision = 15, scale = 0)
  private BigDecimal price;

  @NotNull
  @Convert(converter = StatusConverter.class)
  @Column(nullable = false)
  private Status status;
}
