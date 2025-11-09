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
      "SELECT DISTINCT pd FROM ProductDetail pd "
          + "LEFT JOIN FETCH pd.color "
          + "LEFT JOIN FETCH pd.capacity "
          + "LEFT JOIN FETCH pd.product "
          + "WHERE pd.id = :id")
  java.util.Optional<ProductDetail> findByIdWithDetails(@Param("id") Integer id);

  @Query(
      value =
          "SELECT DISTINCT pd FROM ProductDetail pd "
              + "LEFT JOIN FETCH pd.color "
              + "LEFT JOIN FETCH pd.capacity "
              + "LEFT JOIN FETCH pd.product "
              + "WHERE "
              + "(:sku IS NULL OR LOWER(pd.sku) LIKE LOWER(CONCAT('%', :sku, '%'))) AND "
              + "(:status IS NULL OR pd.status = :status) AND "
              + "(:productId IS NULL OR pd.product.id = :productId) AND "
              + "(:colorId IS NULL OR pd.color.id = :colorId) AND "
              + "(:capacityId IS NULL OR pd.capacity.id = :capacityId)",
      countQuery =
          "SELECT COUNT(DISTINCT pd) FROM ProductDetail pd "
              + "WHERE "
              + "(:sku IS NULL OR LOWER(pd.sku) LIKE LOWER(CONCAT('%', :sku, '%'))) AND "
              + "(:status IS NULL OR pd.status = :status) AND "
              + "(:productId IS NULL OR pd.product.id = :productId) AND "
              + "(:colorId IS NULL OR pd.color.id = :colorId) AND "
              + "(:capacityId IS NULL OR pd.capacity.id = :capacityId)")
  Page<ProductDetail> findAllWithFilters(
      @Param("sku") String sku,
      @Param("status") Status status,
      @Param("productId") Integer productId,
      @Param("colorId") Integer colorId,
      @Param("capacityId") Integer capacityId,
      Pageable pageable);
}
