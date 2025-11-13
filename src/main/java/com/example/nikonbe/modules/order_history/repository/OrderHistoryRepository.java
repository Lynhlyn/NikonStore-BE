package com.example.nikonbe.modules.order_history.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.order_history.entity.OrderHistory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Integer> {

  @Query(
      "SELECT oh FROM OrderHistory oh "
          + "LEFT JOIN oh.order o "
          + "LEFT JOIN oh.customer c "
          + "LEFT JOIN oh.staff s "
          + "WHERE (:trackingNumber IS NULL OR o.trackingNumber LIKE %:trackingNumber%) "
          + "AND (:orderType IS NULL OR o.orderType LIKE %:orderType%) "
          + "AND (:statusAfter IS NULL OR oh.statusAfter = :statusAfter) "
          + "AND (:createdAtFrom IS NULL OR FUNCTION('date', oh.createdAt) >= :createdAtFrom) "
          + "AND (:createdAtTo IS NULL OR FUNCTION('date', oh.createdAt) <= :createdAtTo) "
          + "AND (:changeByName IS NULL OR "
          + "     (c IS NOT NULL AND c.fullName LIKE %:changeByName%) OR "
          + "     (s IS NOT NULL AND s.fullName LIKE %:changeByName%)) "
          + "AND (:notes IS NULL OR oh.notes LIKE %:notes%) "
          + "ORDER BY oh.createdAt DESC")
  Page<OrderHistory> searchOrderHistory(
      @Param("trackingNumber") String trackingNumber,
      @Param("orderType") String orderType,
      @Param("statusAfter") Status statusAfter,
      @Param("createdAtFrom") LocalDate createdAtFrom,
      @Param("createdAtTo") LocalDate createdAtTo,
      @Param("changeByName") String changeByName,
      @Param("notes") String notes,
      Pageable pageable);

  List<OrderHistory> findByOrderId(Integer orderId);
}
