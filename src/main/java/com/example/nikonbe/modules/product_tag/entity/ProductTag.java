package com.example.nikonbe.modules.product_tag.entity;

import com.example.nikonbe.modules.product.entity.Product;
import com.example.nikonbe.modules.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "product_tag")
@IdClass(ProductTagId.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductTag {

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id", nullable = false)
  private Tag tag;
}
