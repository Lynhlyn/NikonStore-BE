package com.example.nikonbe.modules.product_feature.repository;

import com.example.nikonbe.modules.product_feature.entity.ProductFeature;
import com.example.nikonbe.modules.product_feature.entity.ProductFeatureId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductFeatureRepository extends JpaRepository<ProductFeature, ProductFeatureId> {

  boolean existsByProductIdAndFeatureId(Integer productId, Integer featureId);

  @Query("SELECT pf FROM ProductFeature pf WHERE pf.product.id = :productId")
  List<ProductFeature> findByProductId(@Param("productId") Integer productId);

  @Query(
      "SELECT DISTINCT pf FROM ProductFeature pf "
          + "LEFT JOIN FETCH pf.feature "
          + "WHERE pf.product.id IN :productIds")
  List<ProductFeature> findByProductIdIn(@Param("productIds") List<Integer> productIds);

  @Query("SELECT pf FROM ProductFeature pf WHERE pf.feature.id = :featureId")
  List<ProductFeature> findByFeatureId(@Param("featureId") Integer featureId);

  @Modifying
  @Transactional
  @Query("DELETE FROM ProductFeature pf WHERE pf.product.id = :productId")
  void deleteByProductId(@Param("productId") Integer productId);

  @Modifying
  @Transactional
  @Query(
      "DELETE FROM ProductFeature pf WHERE pf.product.id = :productId AND pf.feature.id = :featureId")
  void deleteByProductIdAndFeatureId(
      @Param("productId") Integer productId, @Param("featureId") Integer featureId);
}
