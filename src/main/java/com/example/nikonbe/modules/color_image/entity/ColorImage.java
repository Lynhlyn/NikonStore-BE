package com.example.nikonbe.modules.color_image.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.modules.attributes.color.entity.Color;
import com.example.nikonbe.modules.product.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Entity
@Table(
    name = "color_image",
    uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "color_id"}))
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColorImage extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "product_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_color_image_product"))
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "color_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_color_image_color"))
  private Color color;

  @NotBlank(message = "Image URL is required")
  @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
  private String imageUrl;

  @Column(name = "alt_text", length = 255)
  private String altText;
}
