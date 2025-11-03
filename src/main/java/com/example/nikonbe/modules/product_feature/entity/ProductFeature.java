package com.example.nikonbe.modules.product_feature.entity;

import com.example.nikonbe.modules.feature.entity.Feature;
import com.example.nikonbe.modules.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "product_feature")
@IdClass(ProductFeatureId.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductFeature {

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "feature_id", nullable = false)
  private Feature feature;
}
