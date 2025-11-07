package com.example.nikonbe.modules.product.entity;

import com.example.nikonbe.common.base.BaseEntity;
import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.utils.StatusConverter;
import com.example.nikonbe.modules.attributes.brand.entity.Brand;
import com.example.nikonbe.modules.attributes.category.entity.Category;
import com.example.nikonbe.modules.attributes.material.entity.Material;
import com.example.nikonbe.modules.attributes.strape_type.entity.StrapType;
import com.example.nikonbe.modules.product_image.entity.ProductImage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "product")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  @Column(nullable = false, unique = true)
  private String name;

  @Column private String compartment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "strap_type_id", foreignKey = @ForeignKey(name = "fk_product_strap_type"))
  private StrapType strapType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "brand_id", foreignKey = @ForeignKey(name = "fk_product_brand"))
  private Brand brand;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_product_category"))
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "material_id", foreignKey = @ForeignKey(name = "fk_product_material"))
  private Material material;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column private String dimensions;

  @Column private Double weight;

  @Column private String waterproofRating;

  @NotNull
  @Convert(converter = StatusConverter.class)
  @Column(nullable = false)
  private Status status;

  @OneToMany(
      mappedBy = "product",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<ProductImage> images;
}
