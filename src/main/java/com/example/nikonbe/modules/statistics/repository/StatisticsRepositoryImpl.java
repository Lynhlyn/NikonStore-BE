package com.example.nikonbe.modules.statistics.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.statistics.dto.response.CustomerStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.OrderStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.ProductStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.RevenueStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.SalesChannelStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.TopCustomerStatisticsResponse;
import com.example.nikonbe.modules.statistics.dto.response.VoucherStatisticsResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class StatisticsRepositoryImpl implements StatisticsRepository {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public OrderStatisticsResponse getOrderStatistics(
      LocalDate fromDate,
      LocalDate toDate,
      Integer year,
      Integer month,
      String orderType,
      Status status) {
    String jpql =
        """
        SELECT new com.example.nikonbe.modules.statistics.dto.response.OrderStatisticsResponse(
            COUNT(o.id) as totalOrders,
            COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN 1 END) as completedOrders,
            COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.CANCELLED THEN 1 END) as cancelledOrders,
            COUNT(CASE WHEN o.status IN (com.example.nikonbe.common.enums.Status.PENDING_CONFIRMATION,
                                        com.example.nikonbe.common.enums.Status.CONFIRMED,
                                        com.example.nikonbe.common.enums.Status.PREPARING,
                                        com.example.nikonbe.common.enums.Status.SHIPPING) THEN 1 END) as pendingOrders,
            COALESCE(SUM(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN (o.totalAmount - COALESCE(o.shippingFee, 0)) ELSE 0 END), 0) as totalRevenue,
            COALESCE(CAST(AVG(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN (o.totalAmount - COALESCE(o.shippingFee, 0)) END) AS java.math.BigDecimal), 0) as averageOrderValue,
            COUNT(CASE WHEN o.orderType IN ('ONLINE', 'online') THEN 1 END) as onlineOrders,
            COUNT(CASE WHEN o.orderType = 'IN_STORE' THEN 1 END) as posOrders,
            COALESCE(SUM(CASE WHEN o.orderType IN ('ONLINE', 'online') AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN (o.totalAmount - COALESCE(o.shippingFee, 0)) ELSE 0 END), 0) as onlineRevenue,
            COALESCE(SUM(CASE WHEN o.orderType = 'IN_STORE' AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN (o.totalAmount - COALESCE(o.shippingFee, 0)) ELSE 0 END), 0) as posRevenue
        )
        FROM Order o
        WHERE (:fromDate IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR MONTH(o.createdAt) = :month)
        AND (:orderType IS NULL OR o.orderType = :orderType)
        AND (:status IS NULL OR o.status = :status)
        """;

    Query query = entityManager.createQuery(jpql);
    query.setParameter("fromDate", fromDate);
    query.setParameter("toDate", toDate);
    query.setParameter("year", year);
    query.setParameter("month", month);
    query.setParameter("orderType", orderType);
    query.setParameter("status", status);

    return (OrderStatisticsResponse) query.getSingleResult();
  }

  @Override
  public List<ProductStatisticsResponse> getProductStatistics(
      LocalDate fromDate,
      LocalDate toDate,
      Integer year,
      Integer month,
      Integer categoryId,
      Integer brandId,
      Integer productId) {
    String jpql =
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
        """;

    Query query = entityManager.createQuery(jpql);
    query.setParameter("fromDate", fromDate);
    query.setParameter("toDate", toDate);
    query.setParameter("year", year);
    query.setParameter("month", month);
    query.setParameter("categoryId", categoryId);
    query.setParameter("brandId", brandId);
    query.setParameter("productId", productId);

    return query.getResultList();
  }

  @Override
  public List<RevenueStatisticsResponse> getRevenueStatistics(
      LocalDate fromDate, LocalDate toDate, Integer year, Integer month) {
    String jpql =
        """
        SELECT new com.example.nikonbe.modules.statistics.dto.response.RevenueStatisticsResponse(
            DATE(o.createdAt) as date,
            COALESCE(SUM(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN o.totalAmount ELSE 0 END), 0) as dailyRevenue,
            COALESCE(SUM(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN o.shippingFee ELSE 0 END), 0) as shippingFee,
            COALESCE(SUM(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN (o.totalAmount - COALESCE(o.shippingFee, 0)) ELSE 0 END), 0) as netRevenue,
            COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN 1 END) as dailyOrders,
            COALESCE(SUM(CASE WHEN o.orderType IN ('ONLINE', 'online') AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN (o.totalAmount - COALESCE(o.shippingFee, 0)) ELSE 0 END), 0) as onlineRevenue,
            COALESCE(SUM(CASE WHEN o.orderType = 'IN_STORE' AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN (o.totalAmount - COALESCE(o.shippingFee, 0)) ELSE 0 END), 0) as posRevenue,
            COUNT(CASE WHEN o.orderType IN ('ONLINE', 'online') AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN 1 END) as onlineOrders,
            COUNT(CASE WHEN o.orderType = 'IN_STORE' AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN 1 END) as posOrders
        )
        FROM Order o
        WHERE (:fromDate IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR MONTH(o.createdAt) = :month)
        GROUP BY DATE(o.createdAt)
        ORDER BY DATE(o.createdAt) ASC
        """;

    Query query = entityManager.createQuery(jpql);
    query.setParameter("fromDate", fromDate);
    query.setParameter("toDate", toDate);
    query.setParameter("year", year);
    query.setParameter("month", month);

    return query.getResultList();
  }

  @Override
  public List<CustomerStatisticsResponse> getCustomerStatistics(
      LocalDate fromDate, LocalDate toDate, Integer year, Integer month) {
    String jpql =
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
        """;

    Query query = entityManager.createQuery(jpql);
    query.setParameter("fromDate", fromDate);
    query.setParameter("toDate", toDate);
    query.setParameter("year", year);
    query.setParameter("month", month);

    return query.getResultList();
  }

  @Override
  public List<SalesChannelStatisticsResponse> getSalesChannelStatistics(
      LocalDate fromDate, LocalDate toDate, Integer year, Integer month) {

    BigDecimal totalRevenue = getTotalRevenue(fromDate, toDate, year, month);

    String jpql =
        """
        SELECT new com.example.nikonbe.modules.statistics.dto.response.SalesChannelStatisticsResponse(
            CASE
                WHEN o.orderType IN ('ONLINE', 'online') THEN 'ONLINE'
                WHEN o.orderType = 'IN_STORE' THEN 'IN_STORE'
                ELSE o.orderType
            END as channel,
            COUNT(o.id) as totalOrders,
            COALESCE(SUM(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN (o.totalAmount - COALESCE(o.shippingFee, 0)) ELSE 0 END), 0) as totalRevenue,
            COALESCE(CAST(AVG(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN (o.totalAmount - COALESCE(o.shippingFee, 0)) END) AS java.math.BigDecimal), 0) as averageOrderValue,
            COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN 1 END) as completedOrders,
            COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.CANCELLED THEN 1 END) as cancelledOrders,
            CASE
                WHEN COUNT(o.id) > 0 THEN
                    (COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN 1 END) * 100.0 / COUNT(o.id))
                ELSE 0.0
            END as completionRate,
            CASE
                WHEN :totalRevenue > 0 THEN
                    (COALESCE(SUM(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN (o.totalAmount - COALESCE(o.shippingFee, 0)) ELSE 0 END), 0) * 100.0 / :totalRevenue)
                ELSE 0.0
            END as revenuePercentage
        )
        FROM Order o
        WHERE (:fromDate IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR MONTH(o.createdAt) = :month)
        GROUP BY CASE
            WHEN o.orderType IN ('ONLINE', 'online') THEN 'ONLINE'
            WHEN o.orderType = 'IN_STORE' THEN 'IN_STORE'
            ELSE o.orderType
        END
        """;

    Query query = entityManager.createQuery(jpql);
    query.setParameter("fromDate", fromDate);
    query.setParameter("toDate", toDate);
    query.setParameter("year", year);
    query.setParameter("month", month);
    query.setParameter("totalRevenue", totalRevenue);

    return query.getResultList();
  }

  @Override
  public Long getTotalOrders(LocalDate fromDate, LocalDate toDate, Integer year, Integer month) {
    String jpql =
        """
        SELECT COUNT(DISTINCT o.id)
        FROM Order o
        WHERE (:fromDate IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR MONTH(o.createdAt) = :month)
        """;

    Query query = entityManager.createQuery(jpql);
    query.setParameter("fromDate", fromDate);
    query.setParameter("toDate", toDate);
    query.setParameter("year", year);
    query.setParameter("month", month);

    return (Long) query.getSingleResult();
  }

  @Override
  public java.math.BigDecimal getTotalRevenue(
      LocalDate fromDate, LocalDate toDate, Integer year, Integer month) {
    String jpql =
        """
        SELECT COALESCE(SUM(o.totalAmount - COALESCE(o.shippingFee, 0)), 0)
        FROM Order o
        WHERE o.status = com.example.nikonbe.common.enums.Status.COMPLETED
        AND (:fromDate IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR MONTH(o.createdAt) = :month)
        """;

    Query query = entityManager.createQuery(jpql);
    query.setParameter("fromDate", fromDate);
    query.setParameter("toDate", toDate);
    query.setParameter("year", year);
    query.setParameter("month", month);

    return (java.math.BigDecimal) query.getSingleResult();
  }

  @Override
  public Long getTotalCustomers(LocalDate fromDate, LocalDate toDate, Integer year, Integer month) {
    String jpql =
        """
        SELECT COUNT(c.id)
        FROM Customer c
        WHERE (:fromDate IS NULL OR DATE(c.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(c.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(c.createdAt) = :year)
        AND (:month IS NULL OR MONTH(c.createdAt) = :month)
        """;

    Query query = entityManager.createQuery(jpql);
    query.setParameter("fromDate", fromDate);
    query.setParameter("toDate", toDate);
    query.setParameter("year", year);
    query.setParameter("month", month);

    return (Long) query.getSingleResult();
  }

  @Override
  public Long getTotalActiveProducts() {
    String jpql =
        """
        SELECT COUNT(p.id)
        FROM Product p
        WHERE p.status = com.example.nikonbe.common.enums.Status.ACTIVE
        """;

    Query query = entityManager.createQuery(jpql);
    return (Long) query.getSingleResult();
  }

  @Override
  public List<VoucherStatisticsResponse> getVoucherStatistics(
      LocalDate fromDate, LocalDate toDate, Integer year, Integer month) {
    String jpql =
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
        """;

    Query query = entityManager.createQuery(jpql);
    query.setParameter("fromDate", fromDate);
    query.setParameter("toDate", toDate);
    query.setParameter("year", year);
    query.setParameter("month", month);

    return query.getResultList();
  }

  @Override
  public Long getTotalActiveVouchers() {
    String jpql =
        """
        SELECT COUNT(v.id)
        FROM Voucher v
        WHERE v.status = com.example.nikonbe.common.enums.Status.ACTIVE
        """;

    Query query = entityManager.createQuery(jpql);
    return (Long) query.getSingleResult();
  }

  @Override
  public java.math.BigDecimal getTotalDiscountAmount(
      LocalDate fromDate, LocalDate toDate, Integer year, Integer month) {
    String jpql =
        """
        SELECT COALESCE(SUM(o.discount), 0)
        FROM Order o
        WHERE o.voucher IS NOT NULL
        AND o.status = com.example.nikonbe.common.enums.Status.COMPLETED
        AND (:fromDate IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR MONTH(o.createdAt) = :month)
        """;

    Query query = entityManager.createQuery(jpql);
    query.setParameter("fromDate", fromDate);
    query.setParameter("toDate", toDate);
    query.setParameter("year", year);
    query.setParameter("month", month);

    return (java.math.BigDecimal) query.getSingleResult();
  }

  @Override
  public java.math.BigDecimal getTotalShippingFee(
      LocalDate fromDate, LocalDate toDate, Integer year, Integer month) {
    String jpql =
        """
        SELECT COALESCE(SUM(o.shippingFee), 0)
        FROM Order o
        WHERE o.status = com.example.nikonbe.common.enums.Status.COMPLETED
        AND (:fromDate IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR MONTH(o.createdAt) = :month)
        """;

    Query query = entityManager.createQuery(jpql);
    query.setParameter("fromDate", fromDate);
    query.setParameter("toDate", toDate);
    query.setParameter("year", year);
    query.setParameter("month", month);

    return (java.math.BigDecimal) query.getSingleResult();
  }

  public List<TopCustomerStatisticsResponse> getTopCustomersByCompletedOrders(
      LocalDate fromDate, LocalDate toDate, Integer year, Integer month, int limit) {
    String jpql =
        """
        SELECT new com.example.nikonbe.modules.statistics.dto.response.TopCustomerStatisticsResponse(
            c.id as customerId,
            c.fullName as customerName,
            c.email as email,
            c.phoneNumber as phoneNumber,
            COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN 1 END) as completedOrdersCount,
            COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.CANCELLED THEN 1 END) as cancelledOrdersCount,
            COALESCE(SUM(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN (o.totalAmount - COALESCE(o.shippingFee, 0)) ELSE 0 END), 0) as totalSpent
        )
        FROM Customer c
        INNER JOIN Order o ON o.customer.id = c.id
        WHERE (:fromDate IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR MONTH(o.createdAt) = :month)
        GROUP BY c.id, c.fullName, c.email, c.phoneNumber
        HAVING COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN 1 END) > 0
        ORDER BY COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN 1 END) DESC
        """;

    Query query = entityManager.createQuery(jpql);
    query.setParameter("fromDate", fromDate);
    query.setParameter("toDate", toDate);
    query.setParameter("year", year);
    query.setParameter("month", month);
    query.setMaxResults(limit);

    return query.getResultList();
  }

  public List<TopCustomerStatisticsResponse> getTopCustomersByCancelledOrders(
      LocalDate fromDate, LocalDate toDate, Integer year, Integer month, int limit) {
    String jpql =
        """
        SELECT new com.example.nikonbe.modules.statistics.dto.response.TopCustomerStatisticsResponse(
            c.id as customerId,
            c.fullName as customerName,
            c.email as email,
            c.phoneNumber as phoneNumber,
            COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN 1 END) as completedOrdersCount,
            COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.CANCELLED THEN 1 END) as cancelledOrdersCount,
            COALESCE(SUM(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.COMPLETED THEN (o.totalAmount - COALESCE(o.shippingFee, 0)) ELSE 0 END), 0) as totalSpent
        )
        FROM Customer c
        INNER JOIN Order o ON o.customer.id = c.id
        WHERE (:fromDate IS NULL OR DATE(o.createdAt) >= :fromDate)
        AND (:toDate IS NULL OR DATE(o.createdAt) <= :toDate)
        AND (:year IS NULL OR YEAR(o.createdAt) = :year)
        AND (:month IS NULL OR MONTH(o.createdAt) = :month)
        GROUP BY c.id, c.fullName, c.email, c.phoneNumber
        HAVING COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.CANCELLED THEN 1 END) > 0
        ORDER BY COUNT(CASE WHEN o.status = com.example.nikonbe.common.enums.Status.CANCELLED THEN 1 END) DESC
        """;

    Query query = entityManager.createQuery(jpql);
    query.setParameter("fromDate", fromDate);
    query.setParameter("toDate", toDate);
    query.setParameter("year", year);
    query.setParameter("month", month);
    query.setMaxResults(limit);

    return query.getResultList();
  }
}

