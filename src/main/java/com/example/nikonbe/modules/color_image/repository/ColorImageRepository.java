package com.example.nikonbe.modules.color_image.repository;

import com.example.nikonbe.modules.color_image.entity.ColorImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorImageRepository extends JpaRepository<ColorImage, Integer> {

  boolean existsByProductIdAndColorId(Integer productId, Integer colorId);

  @Query("SELECT ci FROM ColorImage ci WHERE ci.product.id = :productId AND ci.color.id = :colorId")
  Optional<ColorImage> findByProductIdAndColorId(
      @Param("productId") Integer productId, @Param("colorId") Integer colorId);

  @Query("SELECT ci FROM ColorImage ci WHERE ci.product.id = :productId ORDER BY ci.color.id")
  List<ColorImage> findByProductId(@Param("productId") Integer productId);

  @Query(
      "SELECT ci FROM ColorImage ci LEFT JOIN FETCH ci.product p LEFT JOIN FETCH ci.color c WHERE ci.product.id = :productId ORDER BY ci.color.id")
  List<ColorImage> findByProductIdWithDetails(@Param("productId") Integer productId);

  @Query(
      "SELECT ci FROM ColorImage ci LEFT JOIN FETCH ci.product p LEFT JOIN FETCH ci.color c WHERE ci.product.id IN :productIds ORDER BY ci.product.id, ci.color.id")
  List<ColorImage> findByProductIdInWithDetails(@Param("productIds") List<Integer> productIds);

  @Query("SELECT ci FROM ColorImage ci WHERE ci.product.id = :productId AND ci.color.id = :colorId")
  Optional<ColorImage> findByProductIdAndColorIdWithDetails(
      @Param("productId") Integer productId, @Param("colorId") Integer colorId);

  void deleteByProductIdAndColorId(Integer productId, Integer colorId);
}
