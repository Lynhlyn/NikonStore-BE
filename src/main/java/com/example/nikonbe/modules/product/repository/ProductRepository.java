package com.example.nikonbe.modules.product.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.product.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

  boolean existsByNameAndIdNot(String name, Integer id);

  Optional<Product> findByName(String name);

  @Query(
      value =
          "SELECT DISTINCT p FROM Product p "
              + "LEFT JOIN FETCH p.brand "
              + "LEFT JOIN FETCH p.category "
              + "LEFT JOIN FETCH p.material "
              + "LEFT JOIN FETCH p.strapType "
              + "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(p.material.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(p.strapType.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
              + "(:status IS NULL OR p.status = :status) AND "
              + "(:categoryId IS NULL OR p.category.id = :categoryId) AND "
              + "(:brandId IS NULL OR p.brand.id = :brandId) AND "
              + "(:materialId IS NULL OR p.material.id = :materialId) AND "
              + "(:strapTypeId IS NULL OR p.strapType.id = :strapTypeId)",
      countQuery =
          "SELECT COUNT(DISTINCT p) FROM Product p "
              + "LEFT JOIN p.brand "
              + "LEFT JOIN p.category "
              + "LEFT JOIN p.material "
              + "LEFT JOIN p.strapType "
              + "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(p.material.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(p.strapType.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
              + "(:status IS NULL OR p.status = :status) AND "
              + "(:categoryId IS NULL OR p.category.id = :categoryId) AND "
              + "(:brandId IS NULL OR p.brand.id = :brandId) AND "
              + "(:materialId IS NULL OR p.material.id = :materialId) AND "
              + "(:strapTypeId IS NULL OR p.strapType.id = :strapTypeId)")
  Page<Product> findAllWithFilters(
      @Param("keyword") String keyword,
      @Param("status") Status status,
      @Param("categoryId") Integer categoryId,
      @Param("brandId") Integer brandId,
      @Param("materialId") Integer materialId,
      @Param("strapTypeId") Integer strapTypeId,
      Pageable pageable);

  @Query(
      "SELECT DISTINCT p FROM Product p "
          + "LEFT JOIN FETCH p.strapType "
          + "LEFT JOIN FETCH p.brand "
          + "LEFT JOIN FETCH p.category "
          + "LEFT JOIN FETCH p.category.parent "
          + "LEFT JOIN FETCH p.material "
          + "WHERE p.id IN :ids")
  List<Product> findAllWithRelationshipsByIds(@Param("ids") List<Integer> ids);

  @Query(
      "SELECT DISTINCT p FROM Product p "
          + "LEFT JOIN FETCH p.strapType "
          + "LEFT JOIN FETCH p.brand "
          + "LEFT JOIN FETCH p.category "
          + "LEFT JOIN FETCH p.category.parent "
          + "LEFT JOIN FETCH p.material "
          + "WHERE p.id = :id")
  Optional<Product> findByIdWithRelationships(@Param("id") Integer id);

  @Query(
      value =
          "SELECT DISTINCT p FROM Product p "
              + "LEFT JOIN FETCH p.brand "
              + "LEFT JOIN FETCH p.category "
              + "LEFT JOIN FETCH p.material "
              + "LEFT JOIN FETCH p.strapType "
              + "LEFT JOIN com.example.nikonbe.modules.product_tag.entity.ProductTag pt ON pt.product.id = p.id "
              + "LEFT JOIN pt.tag t "
              + "LEFT JOIN com.example.nikonbe.modules.product_feature.entity.ProductFeature pf ON pf.product.id = p.id "
              + "LEFT JOIN pf.feature f "
              + "LEFT JOIN com.example.nikonbe.modules.product_detail.entity.ProductDetail pd ON pd.product.id = p.id "
              + "LEFT JOIN pd.color c "
              + "WHERE "
              + "(:keyword IS NULL OR :keyword = '' OR "
              + "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(p.material.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(pd.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
              + "(:brandIds IS NULL OR p.brand.id IN :brandIds) AND "
              + "(:strapTypeIds IS NULL OR p.strapType.id IN :strapTypeIds) AND "
              + "(:materialIds IS NULL OR p.material.id IN :materialIds) AND "
              + "(:categoryIds IS NULL OR p.category.id IN :categoryIds) AND "
              + "p.status = :status",
      countQuery =
          "SELECT COUNT(DISTINCT p) FROM Product p "
              + "LEFT JOIN com.example.nikonbe.modules.product_tag.entity.ProductTag pt ON pt.product.id = p.id "
              + "LEFT JOIN pt.tag t "
              + "LEFT JOIN com.example.nikonbe.modules.product_feature.entity.ProductFeature pf ON pf.product.id = p.id "
              + "LEFT JOIN pf.feature f "
              + "LEFT JOIN com.example.nikonbe.modules.product_detail.entity.ProductDetail pd ON pd.product.id = p.id "
              + "LEFT JOIN pd.color c "
              + "WHERE "
              + "(:keyword IS NULL OR :keyword = '' OR "
              + "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(p.material.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
              + "LOWER(pd.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
              + "(:brandIds IS NULL OR p.brand.id IN :brandIds) AND "
              + "(:strapTypeIds IS NULL OR p.strapType.id IN :strapTypeIds) AND "
              + "(:materialIds IS NULL OR p.material.id IN :materialIds) AND "
              + "(:categoryIds IS NULL OR p.category.id IN :categoryIds) AND "
              + "p.status = :status")
  Page<Product> findByAdvancedFilters(
      @Param("keyword") String keyword,
      @Param("brandIds") List<Integer> brandIds,
      @Param("strapTypeIds") List<Integer> strapTypeIds,
      @Param("materialIds") List<Integer> materialIds,
      @Param("categoryIds") List<Integer> categoryIds,
      @Param("status") Status status,
      Pageable pageable);
}
