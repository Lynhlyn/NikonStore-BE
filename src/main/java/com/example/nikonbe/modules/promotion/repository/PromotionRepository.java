package com.example.nikonbe.modules.promotion.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.product.entity.Product;
import com.example.nikonbe.modules.product_detail.entity.ProductDetail;
import com.example.nikonbe.modules.promotion.entity.Promotion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
  Optional<Promotion> findByCode(String code);

  boolean existsByCode(String code);

  boolean existsByCodeAndIdNot(String code, Integer id);

  @Query(
      "SELECT p FROM Promotion p WHERE p.status = :status AND p.startDate <= :now AND p.endDate >= :now")
  List<Promotion> findActivePromotions(
      @Param("now") LocalDateTime now, @Param("status") Status status);

  @Query(
      "SELECT p FROM Promotion p WHERE "
          + "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND "
          + "(:code IS NULL OR LOWER(p.code) LIKE LOWER(CONCAT('%', :code, '%'))) AND "
          + "(:status IS NULL OR p.status = :status) AND "
          + "(:discountType IS NULL OR p.discountType = :discountType) AND "
          + "(:appliesTo IS NULL OR p.appliesTo = :appliesTo)")
  Page<Promotion> findPromotionsWithFilters(
      @Param("name") String name,
      @Param("code") String code,
      @Param("status") Status status,
      @Param("discountType") String discountType,
      @Param("appliesTo") String appliesTo,
      Pageable pageable);

  @Query(
      "SELECT p FROM Promotion p WHERE p.status = :status AND p.startDate <= :now AND p.endDate >= :now AND p.appliesTo = :appliesTo")
  List<Promotion> findActivePromotionsByAppliesTo(
      @Param("now") LocalDateTime now,
      @Param("status") Status status,
      @Param("appliesTo") String appliesTo);

  @Query(
      "SELECT p FROM Promotion p WHERE p.status = :status AND p.startDate <= :now AND p.endDate >= :now AND p.appliesTo = :appliesTo AND p.appliedProduct = :productId")
  List<Promotion> findActivePromotionsForProduct(
      @Param("now") LocalDateTime now,
      @Param("status") Status status,
      @Param("appliesTo") String appliesTo,
      @Param("productId") String productId);

  @Query(
      "SELECT pd FROM ProductDetail pd "
          + "LEFT JOIN FETCH pd.product p "
          + "LEFT JOIN FETCH pd.color c "
          + "LEFT JOIN FETCH pd.capacity cap "
          + "LEFT JOIN FETCH pd.promotion pr "
          + "WHERE pd.promotion.id = :promotionId "
          + "AND pd.status != com.example.nikonbe.common.enums.Status.DELETED")
  List<ProductDetail> findByPromotionId(@Param("promotionId") Integer promotionId);

  @Query(
      "SELECT DISTINCT p FROM Product p "
          + "LEFT JOIN FETCH p.brand b "
          + "LEFT JOIN FETCH p.category c "
          + "LEFT JOIN FETCH p.material m "
          + "LEFT JOIN FETCH p.strapType st "
          + "WHERE p.id IN ("
          + "  SELECT pd.product.id FROM ProductDetail pd "
          + "  WHERE pd.promotion.id = :promotionId "
          + "  AND pd.status != com.example.nikonbe.common.enums.Status.DELETED"
          + ") "
          + "AND p.status != com.example.nikonbe.common.enums.Status.DELETED")
  List<Product> findProductsByPromotionId(@Param("promotionId") Integer promotionId);

  @Query(
      "SELECT pd FROM ProductDetail pd WHERE pd.promotion.id = :promotionId AND pd.status != com.example.nikonbe.common.enums.Status.DELETED")
  List<ProductDetail> findActiveProductDetailsByPromotionId(
      @Param("promotionId") Integer promotionId);

  @Query(
      "SELECT COUNT(pd) FROM ProductDetail pd WHERE pd.promotion.id = :promotionId AND pd.status != com.example.nikonbe.common.enums.Status.DELETED")
  Long countActiveProductDetailsByPromotionId(@Param("promotionId") Integer promotionId);
}
