package com.example.nikonbe.modules.product_tag.repository;

import com.example.nikonbe.modules.product_tag.entity.ProductTag;
import com.example.nikonbe.modules.product_tag.entity.ProductTagId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTagRepository extends JpaRepository<ProductTag, ProductTagId> {

  boolean existsByProductIdAndTagId(Integer productId, Integer tagId);

  List<ProductTag> findByProductId(Integer productId);

  List<ProductTag> findByTagId(Integer tagId);

  void deleteByProductId(Integer productId);
}
