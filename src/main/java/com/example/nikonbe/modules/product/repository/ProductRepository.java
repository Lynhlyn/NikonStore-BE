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
      "SELECT p FROM Product p WHERE (:status IS NULL OR p.status = :status) AND "
          + "(:categoryId IS NULL OR p.category.id = :categoryId) AND "
          + "(:brandId IS NULL OR p.brand.id = :brandId)")
  Page<Product> findAllWithFilters(
      @Param("status") Status status,
      @Param("categoryId") Integer categoryId,
      @Param("brandId") Integer brandId,
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
}
