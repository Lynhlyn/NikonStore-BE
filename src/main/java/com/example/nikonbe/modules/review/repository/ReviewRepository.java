package com.example.nikonbe.modules.review.repository;

import com.example.nikonbe.modules.review.entity.Review;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

  Page<Review> findByProductIdAndStatus(Integer productId, Integer status, Pageable pageable);

  Page<Review> findByProductId(Integer productId, Pageable pageable);

  Page<Review> findByCustomerId(Integer customerId, Pageable pageable);

  @Query(
      "SELECT r FROM Review r "
          + "LEFT JOIN FETCH r.customer "
          + "LEFT JOIN FETCH r.product "
          + "LEFT JOIN FETCH r.orderDetail od "
          + "LEFT JOIN FETCH od.order "
          + "WHERE r.product.id = :productId AND r.status = :status "
          + "ORDER BY r.createdAt DESC")
  Page<Review> findByProductIdAndStatusWithRelations(
      @Param("productId") Integer productId, @Param("status") Integer status, Pageable pageable);

  @Query(
      "SELECT r FROM Review r "
          + "LEFT JOIN FETCH r.customer "
          + "LEFT JOIN FETCH r.product "
          + "LEFT JOIN FETCH r.orderDetail od "
          + "LEFT JOIN FETCH od.order "
          + "WHERE r.product.id = :productId "
          + "ORDER BY r.createdAt DESC")
  Page<Review> findByProductIdWithRelations(
      @Param("productId") Integer productId, Pageable pageable);

  @Query(
      "SELECT r FROM Review r "
          + "LEFT JOIN FETCH r.customer "
          + "LEFT JOIN FETCH r.product "
          + "LEFT JOIN FETCH r.orderDetail od "
          + "LEFT JOIN FETCH od.order "
          + "WHERE r.id = :id")
  Optional<Review> findByIdWithRelations(@Param("id") Integer id);

  @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.status = 1")
  Double getAverageRatingByProductId(@Param("productId") Integer productId);

  @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.status = 1")
  Long getReviewCountByProductId(@Param("productId") Integer productId);

  boolean existsByOrderDetailId(Integer orderDetailId);

  @Query(
      "SELECT r FROM Review r "
          + "LEFT JOIN FETCH r.customer "
          + "LEFT JOIN FETCH r.product "
          + "LEFT JOIN FETCH r.orderDetail od "
          + "LEFT JOIN FETCH od.order "
          + "WHERE r.orderDetail.order.id = :orderId "
          + "ORDER BY r.createdAt DESC")
  List<Review> findByOrderIdWithRelations(@Param("orderId") Integer orderId);
}
