package com.example.nikonbe.modules.orders.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.orders.entity.Order;
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
public interface OrderRepository extends JpaRepository<Order, Integer> {

  Page<Order> findByCustomer_Id(Integer customerId, Pageable pageable);

  Page<Order> findByCustomer_IdAndStatus(Integer customerId, Status status, Pageable pageable);

  @Query(
      "SELECT o FROM Order o WHERE o.customer.id = :customerId "
          + "AND FUNCTION('date', o.createdAt) >= :fromDate "
          + "AND FUNCTION('date', o.createdAt) <= :toDate")
  Page<Order> findByCustomer_IdAndCreatedDateBetween(
      @Param("customerId") Integer customerId,
      @Param("fromDate") java.time.LocalDate fromDate,
      @Param("toDate") java.time.LocalDate toDate,
      Pageable pageable);

  @Query(
      "SELECT o FROM Order o WHERE o.customer.id = :customerId "
          + "AND o.status = :status "
          + "AND FUNCTION('date', o.createdAt) >= :fromDate "
          + "AND FUNCTION('date', o.createdAt) <= :toDate")
  Page<Order> findByCustomer_IdAndStatusAndCreatedDateBetween(
      @Param("customerId") Integer customerId,
      @Param("status") Status status,
      @Param("fromDate") java.time.LocalDate fromDate,
      @Param("toDate") java.time.LocalDate toDate,
      Pageable pageable);

  @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderDetails WHERE o.id = :orderId")
  Optional<Order> findByIdWithDetails(@Param("orderId") Integer orderId);

  Optional<Order> findByTrackingNumber(String trackingNumber);

  @Query(
      value =
          "SELECT o FROM Order o WHERE "
              + "(:keyword IS NULL OR LOWER(o.trackingNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) "
              + "OR LOWER(o.recipientName) LIKE LOWER(CONCAT('%', :keyword, '%')) "
              + "OR o.recipientPhone LIKE CONCAT('%', :keyword, '%')) "
              + "AND (:type IS NULL OR o.orderType = :type) "
              + "AND (:status IS NULL OR o.status = :status) "
              + "AND (:fromDate IS NULL OR FUNCTION('date', o.createdAt) >= :fromDate) "
              + "AND (:toDate IS NULL OR FUNCTION('date', o.createdAt) <= :toDate)"
              + "ORDER BY CASE o.status "
              + "WHEN com.example.nikonbe.common.enums.Status.PENDING_CONFIRMATION THEN 1 "
              + "WHEN com.example.nikonbe.common.enums.Status.CONFIRMED THEN 2 "
              + "WHEN com.example.nikonbe.common.enums.Status.PREPARING THEN 3 "
              + "WHEN com.example.nikonbe.common.enums.Status.SHIPPING THEN 4 "
              + "WHEN com.example.nikonbe.common.enums.Status.COMPLETED THEN 5 "
              + "WHEN com.example.nikonbe.common.enums.Status.FAILED_DELIVERY THEN 6 "
              + "WHEN com.example.nikonbe.common.enums.Status.CANCELLED THEN 7 "
              + "WHEN com.example.nikonbe.common.enums.Status.PENDING_PAYMENT THEN 8 "
              + "ELSE 9 END, o.createdAt DESC")
  Page<Order> searchOrders(
      @Param("keyword") String keyword,
      @Param("type") String type,
      @Param("status") Status status,
      @Param("fromDate") java.time.LocalDate fromDate,
      @Param("toDate") java.time.LocalDate toDate,
      Pageable pageable);

  Page<Order> findByStatusAndOrderType(Status status, String orderType, Pageable pageable);

  @Query(
      "SELECT o FROM Order o WHERE o.status = :status AND o.orderType = :orderType "
          + "AND o.createdAt < :cutoffTime AND o.id IS NOT NULL")
  List<Order> findOldPendingOrders(
      @Param("status") Status status,
      @Param("orderType") String orderType,
      @Param("cutoffTime") LocalDateTime cutoffTime);

  Page<Order> findByStaff_IdAndStatus(Integer staffId, Status status, Pageable pageable);

  @Query(
      "SELECT o FROM Order o WHERE o.status = :status AND LOWER(o.paymentMethod) = LOWER(:paymentMethod) AND o.createdAt < :threshold")
  List<Order> findUnpaidVnpayOrdersBefore(
      @Param("status") Status status,
      @Param("paymentMethod") String paymentMethod,
      @Param("threshold") LocalDateTime threshold);

  @Query(
      "SELECT o FROM Order o WHERE o.status = :status AND o.orderType = :orderType "
          + "AND (:customerId IS NULL OR o.customer.id = :customerId) "
          + "AND (:staffId IS NULL OR o.staff.id = :staffId)")
  Page<Order> findPendingPOSOrders(
      @Param("status") Status status,
      @Param("orderType") String orderType,
      @Param("customerId") Integer customerId,
      @Param("staffId") Integer staffId,
      Pageable pageable);

  int countByStaffIdAndStatusAndOrderType(Integer staffId, Status status, String orderType);
}
