package com.example.nikonbe.modules.product_feature.entity;

import java.io.Serializable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFeatureId implements Serializable {

  private Integer product;

  private Integer feature;
}
