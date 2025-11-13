package com.example.nikonbe.modules.order_detail.repository;

import com.example.nikonbe.modules.order_detail.entity.OrderDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
  @Query(
      "SELECT od FROM OrderDetail od "
          + "JOIN FETCH od.productDetail pd "
          + "JOIN FETCH pd.product p "
          + "LEFT JOIN FETCH p.brand b "
          + "LEFT JOIN FETCH p.category c "
          + "LEFT JOIN FETCH pd.color col "
          + "LEFT JOIN FETCH pd.capacity cap "
          + "WHERE od.order.id = :orderId")
  List<OrderDetail> findByOrderIdWithDetails(@Param("orderId") Integer orderId);

  @Query("SELECT od FROM OrderDetail od WHERE od.order.id = :orderId")
  List<OrderDetail> findByOrderId(@Param("orderId") Integer orderId);
}
