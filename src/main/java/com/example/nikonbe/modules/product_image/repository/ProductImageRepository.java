package com.example.nikonbe.modules.product_image.repository;

import com.example.nikonbe.modules.product_image.entity.ProductImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

  List<ProductImage> findByProductIdOrderBySortOrderAsc(Integer productId);

  @Query(
      "SELECT pi FROM ProductImage pi WHERE pi.product.id IN :productIds ORDER BY pi.product.id, pi.sortOrder ASC")
  List<ProductImage> findByProductIdInOrderByProductIdAndSortOrderAsc(
      @Param("productIds") List<Integer> productIds);

  Optional<ProductImage> findByProductIdAndIsPrimaryTrue(Integer productId);

  boolean existsByProductIdAndIsPrimaryTrue(Integer productId);

  @Modifying
  @Transactional
  @Query("DELETE FROM ProductImage pi WHERE pi.product.id = :productId")
  void deleteByProductId(@Param("productId") Integer productId);

  @Modifying
  @Transactional
  @Query("UPDATE ProductImage pi SET pi.isPrimary = false WHERE pi.product.id = :productId")
  void unsetPrimaryByProductId(@Param("productId") Integer productId);
}
