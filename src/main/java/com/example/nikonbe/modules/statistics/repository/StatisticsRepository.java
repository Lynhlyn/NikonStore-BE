package com.example.nikonbe.modules.statistics.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.statistics.dto.response.CustomerStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.OrderStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.ProductStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.RevenueStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.SalesChannelStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.VoucherStatisticsResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticsRepository {

  @Query(
      """
        SELECT new com.example.nikonbe.modules.statistics.dto.response.OrderStatisticsResponse(
            COUNT(o.id) as totalOrders,
            COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN 1 END) as completedOrders,
            COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.CANCELLED THEN 1 END) as cancelledOrders,
            COUNT(CASE WHEN o.status IN (com.example.nikonbe.common.enums.Status.PENDING_CONFIRMATION,
                                        com.example.nikonbe.common.enums.Status.CONFIRMED,
                                        com.example.nikonbe.common.enums.Status.PREPARING,
                                        com.example.nikonbe.common.enums.Status.SHIPPING) THEN 1 END) as pendingOrders,
            COALESCE(SUM(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN o.totalAmount ELSE 0 END), 0) as totalRevenue,
            COALESCE(CAST(AVG(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN o.totalAmount END) AS java.math.BigDecimal), 0) as averageOrderValue,
            COUNT(CASE WHEN o.orderType = 'ONLINE' THEN 1 END) as onlineOrders,
            COUNT(CASE WHEN o.orderType = 'IN_STORE' THEN 1 END) as posOrders,
            COALESCE(SUM(CASE WHEN o.orderType = 'ONLINE' AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN o.totalAmount ELSE 0 END), 0) as onlineRevenue,
            COALESCE(SUM(CASE WHEN o.orderType = 'IN_STORE' AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN o.totalAmount ELSE 0 END), 0) as posRevenue
        )
        FROM Order o
        WHERE (:fromDate IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR MONTH(o.createdAt) = :month)
        AND (:orderType IS NULL OR o.orderType = :orderType)
        AND (:status IS NULL OR o.status = :status)
        """)
  OrderStatisticsResponse getOrderStatistics(
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate,
      @Param("year") Integer year,
      @Param("month") Integer month,
      @Param("orderType") String orderType,
      @Param("status") Status status);

  @Query(
      """
        SELECT new com.example.nikonbe.modules.statistics.dto.response.ProductStatisticsResponse(
            p.id as productId,
            p.name as productName,
            b.name as brandName,
            c.name as categoryName,
            COALESCE(SUM(od.quantity), 0) as totalSold,
            COALESCE(SUM(od.quantity * od.price), 0) as totalRevenue,
            COALESCE(CAST(AVG(od.price) AS java.math.BigDecimal), 0) as averagePrice,
            COUNT(DISTINCT o.id) as orderCount
        )
        FROM Product p
        LEFT JOIN p.brand b
        LEFT JOIN p.category c
        LEFT JOIN ProductDetail pd ON pd.product.id = p.id
        LEFT JOIN OrderDetail od ON od.productDetail.id = pd.id
        LEFT JOIN Order o ON od.order.id = o.id AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED
        WHERE (:fromDate IS NULL OR o.createdAt IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR o.createdAt IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR o.createdAt IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR o.createdAt IS NULL OR MONTH(o.createdAt) = :month)
        AND (:categoryId IS NULL OR p.category.id = :categoryId)
        AND (:brandId IS NULL OR p.brand.id = :brandId)
        AND (:productId IS NULL OR p.id = :productId)
        GROUP BY p.id, p.name, b.name, c.name
        HAVING SUM(od.quantity) > 0
        ORDER BY SUM(od.quantity) DESC
        """)
  List<ProductStatisticsResponse> getProductStatistics(
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate,
      @Param("year") Integer year,
      @Param("month") Integer month,
      @Param("categoryId") Integer categoryId,
      @Param("brandId") Integer brandId,
      @Param("productId") Integer productId);

  @Query(
      """
        SELECT new com.example.nikonbe.modules.statistics.dto.response.RevenueStatisticsResponse(
            DATE(o.createdAt) as date,
            COALESCE(SUM(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN o.totalAmount ELSE 0 END), 0) as dailyRevenue,
            COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN 1 END) as dailyOrders,
            COALESCE(SUM(CASE WHEN o.orderType = 'ONLINE' AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN o.totalAmount ELSE 0 END), 0) as onlineRevenue,
            COALESCE(SUM(CASE WHEN o.orderType = 'IN_STORE' AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN o.totalAmount ELSE 0 END), 0) as posRevenue,
            COUNT(CASE WHEN o.orderType = 'ONLINE' AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN 1 END) as onlineOrders,
            COUNT(CASE WHEN o.orderType = 'IN_STORE' AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN 1 END) as posOrders
        )
        FROM Order o
        WHERE (:fromDate IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR MONTH(o.createdAt) = :month)
        GROUP BY DATE(o.createdAt)
        ORDER BY DATE(o.createdAt) ASC
        """)
  List<RevenueStatisticsResponse> getRevenueStatistics(
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate,
      @Param("year") Integer year,
      @Param("month") Integer month);

  @Query(
      """
        SELECT new com.example.nikonbe.modules.statistics.dto.response.CustomerStatisticsResponse(
            DATE(c.createdAt) as date,
            COUNT(c.id) as newCustomers,
            (SELECT COUNT(c2.id) FROM Customer c2 WHERE DATE(c2.createdAt) <= DATE(c.createdAt)) as totalCustomers,
            (SELECT COUNT(DISTINCT o.customer.id) FROM Order o WHERE DATE(o.createdAt) = DATE(c.createdAt) AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED) as activeCustomers,
            COUNT(CASE WHEN c.isGuest = true THEN 1 END) as guestCustomers
        )
        FROM Customer c
        WHERE (:fromDate IS NULL OR DATE(c.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(c.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(c.createdAt) = :year)
        AND (:month IS NULL OR MONTH(c.createdAt) = :month)
        GROUP BY DATE(c.createdAt), c.createdAt
        ORDER BY DATE(c.createdAt) ASC
        """)
  List<CustomerStatisticsResponse> getCustomerStatistics(
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate,
      @Param("year") Integer year,
      @Param("month") Integer month);

  @Query(
      """
        SELECT new com.example.nikonbe.modules.statistics.dto.response.SalesChannelStatisticsResponse(
            o.orderType as channel,
            COUNT(o.id) as totalOrders,
            COALESCE(SUM(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN o.totalAmount ELSE 0 END), 0) as totalRevenue,
            COALESCE(CAST(AVG(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN o.totalAmount END) AS java.math.BigDecimal), 0) as averageOrderValue,
            COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN 1 END) as completedOrders,
            COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.CANCELLED THEN 1 END) as cancelledOrders,
            CASE
                WHEN COUNT(o.id) > 0 THEN
                    (COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN 1 END) * 100.0 / COUNT(o.id))
                ELSE 0.0
            END as completionRate
        )
        FROM Order o
        WHERE (:fromDate IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR MONTH(o.createdAt) = :month)
        GROUP BY o.orderType
        """)
  List<SalesChannelStatisticsResponse> getSalesChannelStatistics(
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate,
      @Param("year") Integer year,
      @Param("month") Integer month);

  @Query(
      """
        SELECT COUNT(DISTINCT o.id)
        FROM Order o
        WHERE (:fromDate IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR MONTH(o.createdAt) = :month)
        """)
  Long getTotalOrders(
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate,
      @Param("year") Integer year,
      @Param("month") Integer month);

  @Query(
      """
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.status = com.example.nikonbe.common.enums.Status.COMPLETED
        AND (:fromDate IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR MONTH(o.createdAt) = :month)
        """)
  java.math.BigDecimal getTotalRevenue(
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate,
      @Param("year") Integer year,
      @Param("month") Integer month);

  @Query(
      """
        SELECT COUNT(c.id)
        FROM Customer c
        WHERE (:fromDate IS NULL OR DATE(c.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(c.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(c.createdAt) = :year)
        AND (:month IS NULL OR MONTH(c.createdAt) = :month)
        """)
  Long getTotalCustomers(
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate,
      @Param("year") Integer year,
      @Param("month") Integer month);

  @Query(
      """
        SELECT COUNT(p.id)
        FROM Product p
        WHERE p.status = com.example.nikonbe.common.enums.Status.ACTIVE
        """)
  Long getTotalActiveProducts();

  @Query(
      """
        SELECT new com.example.nikonbe.modules.statistics.dto.response.VoucherStatisticsResponse(
            v.id as voucherId,
            v.code as voucherCode,
            v.description as description,
            v.discountType as discountType,
            v.discountValue as discountValue,
            v.quantity as totalQuantity,
            v.usedCount as usedCount,
            (v.quantity - v.usedCount) as remainingQuantity,
            COALESCE(SUM(o.discount), 0) as totalDiscountAmount,
            COUNT(DISTINCT o.id) as orderCount,
            DATE(v.startDate) as startDate,
            DATE(v.endDate) as endDate,
            CASE v.status
                WHEN com.example.nikonbe.common.enums.Status.ACTIVE THEN 'ACTIVE'
                WHEN com.example.nikonbe.common.enums.Status.INACTIVE THEN 'INACTIVE'
                ELSE 'UNKNOWN'
            END as status
        )
        FROM Voucher v
        LEFT JOIN Order o ON o.voucher.id = v.id AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED
        WHERE (:fromDate IS NULL OR o.createdAt IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR o.createdAt IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR o.createdAt IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR o.createdAt IS NULL OR MONTH(o.createdAt) = :month)
        GROUP BY v.id, v.code, v.description, v.discountType, v.discountValue,
                 v.quantity, v.usedCount, v.startDate, v.endDate, v.status
        ORDER BY v.usedCount DESC
        """)
  List<VoucherStatisticsResponse> getVoucherStatistics(
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate,
      @Param("year") Integer year,
      @Param("month") Integer month);

  @Query(
      """
        SELECT COUNT(v.id)
        FROM Voucher v
        WHERE v.status = com.example.nikonbe.common.enums.Status.ACTIVE
        """)
  Long getTotalActiveVouchers();

  @Query(
      """
        SELECT COALESCE(SUM(o.discount), 0)
        FROM Order o
        WHERE o.voucher IS NOT NULL
        AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED
        AND (:fromDate IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR MONTH(o.createdAt) = :month)
        """)
  java.math.BigDecimal getTotalDiscountAmount(
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate,
      @Param("year") Integer year,
      @Param("month") Integer month);
}

