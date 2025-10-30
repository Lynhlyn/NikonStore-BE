package com.example.nikonbe.modules.product_detail.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.product_detail.entity.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer> {

  boolean existsBySkuAndIdNot(String sku, Integer id);

  @Query(
      "SELECT pd FROM ProductDetail pd WHERE "
          + "(:status IS NULL OR pd.status = :status) AND "
          + "(:productId IS NULL OR pd.product.id = :productId) AND "
          + "(:colorId IS NULL OR pd.color.id = :colorId) AND "
          + "(:capacityId IS NULL OR pd.capacity.id = :capacityId)")
  Page<ProductDetail> findAllWithFilters(
      @Param("status") Status status,
      @Param("productId") Integer productId,
      @Param("colorId") Integer colorId,
      @Param("capacityId") Integer capacityId,
      Pageable pageable);
}
