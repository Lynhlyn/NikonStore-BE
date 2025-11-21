package com.example.nikonbe.modules.product_detail.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.product_detail.entity.ProductDetail;
import java.math.BigDecimal;
import java.util.List;
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

  @Query(
      "SELECT pd FROM ProductDetail pd "
          + "LEFT JOIN FETCH pd.product p "
          + "LEFT JOIN FETCH pd.color c "
          + "LEFT JOIN FETCH pd.capacity cap "
          + "LEFT JOIN FETCH pd.promotion pr "
          + "WHERE "
          + "(:productId IS NULL OR pd.product.id = :productId) AND "
          + "(:sku IS NULL OR pd.sku LIKE %:sku%) AND "
          + "(:colorId IS NULL OR pd.color.id = :colorId) AND "
          + "(:capacityId IS NULL OR pd.capacity.id = :capacityId) AND "
          + "(:status IS NULL OR pd.status = :status) AND "
          + "(:minPrice IS NULL OR pd.price >= :minPrice) AND "
          + "(:maxPrice IS NULL OR pd.price <= :maxPrice) AND "
          + "(:promotionId IS NULL OR pd.promotion.id = :promotionId)")
  Page<ProductDetail> findByFilters(
      @Param("productId") Integer productId,
      @Param("sku") String sku,
      @Param("colorId") Integer colorId,
      @Param("capacityId") Integer capacityId,
      @Param("status") Status status,
      @Param("minPrice") BigDecimal minPrice,
      @Param("maxPrice") BigDecimal maxPrice,
      @Param("promotionId") Integer promotionId,
      Pageable pageable);

  @Query(
      "SELECT pd FROM ProductDetail pd "
          + "LEFT JOIN FETCH pd.product p "
          + "LEFT JOIN FETCH pd.color c "
          + "LEFT JOIN FETCH pd.capacity cap "
          + "LEFT JOIN FETCH pd.promotion pr "
          + "WHERE pd.sku = :sku AND pd.status = :status "
          + "ORDER BY pd.id DESC")
  List<ProductDetail> findBySkuAndStatus(@Param("sku") String sku, @Param("status") Status status);

  @Query("SELECT pd FROM ProductDetail pd WHERE pd.product.id = :productId AND pd.status = :status")
  List<ProductDetail> findByProductIdAndStatus(
      @Param("productId") Integer productId, @Param("status") Status status);

  @Query(
      "SELECT MIN(pd.price) FROM ProductDetail pd WHERE pd.product.id = :productId AND pd.status = :status AND pd.stock > 0")
  BigDecimal findMinPriceByProductIdAndStatus(
      @Param("productId") Integer productId, @Param("status") Status status);

  @Query(
      "SELECT MAX(pd.price) FROM ProductDetail pd WHERE pd.product.id = :productId AND pd.status = :status AND pd.stock > 0")
  BigDecimal findMaxPriceByProductIdAndStatus(
      @Param("productId") Integer productId, @Param("status") Status status);
}
