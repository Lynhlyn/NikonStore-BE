package com.example.nikonbe.modules.product_tag.repository;

import com.example.nikonbe.modules.product_tag.entity.ProductTag;
import com.example.nikonbe.modules.product_tag.entity.ProductTagId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTagRepository extends JpaRepository<ProductTag, ProductTagId> {

  boolean existsByProductIdAndTagId(Integer productId, Integer tagId);

  List<ProductTag> findByProductId(Integer productId);

  @Query(
      "SELECT DISTINCT pt FROM ProductTag pt "
          + "LEFT JOIN FETCH pt.tag "
          + "WHERE pt.product.id IN :productIds")
  List<ProductTag> findByProductIdIn(@Param("productIds") List<Integer> productIds);

  List<ProductTag> findByTagId(Integer tagId);

  void deleteByProductId(Integer productId);
}
