package com.example.nikonbe.modules.product_tag.entity;

import java.io.Serializable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductTagId implements Serializable {

  private Integer product;

  private Integer tag;
}
