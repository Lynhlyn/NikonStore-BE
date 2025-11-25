package com.example.nikonbe.modules.promotion.scheduler;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.promotion.entity.Promotion;
import com.example.nikonbe.modules.promotion.repository.PromotionRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Scheduler tự động cập nhật trạng thái của các promotion dựa trên thời gian Chạy mỗi 10 giây để đảm
 * bảo trạng thái promotion được cập nhật kịp thời và tránh lạm dụng promotion quá hạn
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionStatusScheduler {

  private final PromotionRepository promotionRepository;

  /**
   * Automatically updates promotion status every 10 seconds. Update logic: - Promotion chưa bắt đầu
   * (startDate > now) -> PENDING_START - Promotion đã hết hạn (endDate < now) -> INACTIVE -
   * Promotion trong thời gian hiệu lực và status không phải DELETED -> ACTIVE
   */
  @Scheduled(fixedRate = 10000) // Chạy mỗi 10 giây
  @Transactional
  public void updatePromotionStatus() {
    log.debug("Starting scheduled promotion status update");

    try {
      LocalDateTime now = LocalDateTime.now();
      int updatedCount = 0;

      // 1. Cập nhật promotion chưa đến thời gian bắt đầu thành PENDING_START
      updatedCount += updatePendingPromotions(now);

      // 2. Cập nhật promotion đã hết hạn thành INACTIVE
      updatedCount += updateExpiredPromotions(now);

      // 3. Kích hoạt lại promotion đang trong thời gian sử dụng
      updatedCount += activateAvailablePromotions(now);

      if (updatedCount > 0) {
        log.info("Completed promotion status update. Updated {} promotions", updatedCount);
      } else {
        log.debug("No promotion status updates needed");
      }

    } catch (Exception e) {
      log.error("Error during scheduled promotion status update", e);
    }
  }

  /** Cập nhật promotion chưa đến thời gian bắt đầu thành PENDING_START */
  private int updatePendingPromotions(LocalDateTime now) {
    List<Promotion> pendingPromotions =
        promotionRepository.findAll().stream()
            .filter(p -> p.getStatus() == Status.ACTIVE && p.getStartDate().isAfter(now))
            .toList();

    if (!pendingPromotions.isEmpty()) {
      pendingPromotions.forEach(
          promotion -> {
            promotion.setStatus(Status.PENDING_START);
            log.debug(
                "Promotion {} chưa đến thời gian sử dụng, cập nhật thành PENDING_START",
                promotion.getName());
          });
      promotionRepository.saveAll(pendingPromotions);
      return pendingPromotions.size();
    }
    return 0;
  }

  /** Cập nhật promotion đã hết hạn thành INACTIVE */
  private int updateExpiredPromotions(LocalDateTime now) {
    List<Promotion> expiredPromotions =
        promotionRepository.findAll().stream()
            .filter(
                p ->
                    (p.getStatus() == Status.ACTIVE || p.getStatus() == Status.PENDING_START)
                        && p.getEndDate().isBefore(now))
            .toList();

    if (!expiredPromotions.isEmpty()) {
      expiredPromotions.forEach(
          promotion -> {
            promotion.setStatus(Status.INACTIVE);
            log.debug("Promotion {} đã hết hạn, cập nhật thành INACTIVE", promotion.getName());
          });
      promotionRepository.saveAll(expiredPromotions);
      return expiredPromotions.size();
    }
    return 0;
  }

  /** Kích hoạt lại promotion từ PENDING_START thành ACTIVE khi đến thời gian */
  private int activateAvailablePromotions(LocalDateTime now) {
    List<Promotion> availablePromotions =
        promotionRepository.findAll().stream()
            .filter(
                p -> {
                  // CHỈ xử lý promotion có status PENDING_START (không cho phép từ INACTIVE)
                  if (p.getStatus() != Status.PENDING_START) {
                    return false;
                  }

                  // Kiểm tra đã đến thời gian bắt đầu
                  boolean hasStarted = !p.getStartDate().isAfter(now);

                  // Kiểm tra chưa hết hạn
                  boolean notExpired = !p.getEndDate().isBefore(now);

                  return hasStarted && notExpired;
                })
            .toList();

    if (!availablePromotions.isEmpty()) {
      availablePromotions.forEach(
          promotion -> {
            // Chỉ chuyển từ PENDING_START thành ACTIVE
            promotion.setStatus(Status.ACTIVE);
            log.debug(
                "Promotion {} đã đến thời gian sử dụng, kích hoạt thành ACTIVE",
                promotion.getName());
          });
      promotionRepository.saveAll(availablePromotions);
      return availablePromotions.size();
    }
    return 0;
  }

  /**
   * Chạy ngay lập tức để cập nhật trạng thái promotion khi ứng dụng khởi động Hữu ích khi server
   * restart và cần sync lại trạng thái
   */
  @Scheduled(initialDelay = 10000, fixedRate = Long.MAX_VALUE) // Chạy 1 lần sau 10s khởi động
  @Transactional
  public void initialPromotionStatusUpdate() {
    log.info("Running initial promotion status update on application startup");
    updatePromotionStatus();
  }

  /**
   * Scheduler kiểm tra và log thống kê promotion (chạy mỗi giờ) Giúp monitor tình trạng promotion
   * trong hệ thống
   */
  @Scheduled(fixedRate = 3600000) // Chạy mỗi 1 giờ
  @Transactional(readOnly = true)
  public void logPromotionStatistics() {
    try {
      LocalDateTime now = LocalDateTime.now();

      List<Promotion> allPromotions = promotionRepository.findAll();

      long activeCount =
          allPromotions.stream().filter(p -> Status.ACTIVE.equals(p.getStatus())).count();

      long inactiveCount =
          allPromotions.stream().filter(p -> Status.INACTIVE.equals(p.getStatus())).count();

      long deletedCount =
          allPromotions.stream().filter(p -> Status.DELETED.equals(p.getStatus())).count();

      long expiredCount =
          allPromotions.stream()
              .filter(
                  p ->
                      !Status.DELETED.equals(p.getStatus())
                          && p.getEndDate() != null
                          && p.getEndDate().isBefore(now))
              .count();

      long pendingCount =
          allPromotions.stream()
              .filter(
                  p ->
                      !Status.DELETED.equals(p.getStatus())
                          && p.getStartDate() != null
                          && p.getStartDate().isAfter(now))
              .count();

      log.info(
          "Promotion Statistics - Total: {}, Active: {}, Inactive: {}, "
              + "Deleted: {}, Expired: {}, Pending: {}",
          allPromotions.size(),
          activeCount,
          inactiveCount,
          deletedCount,
          expiredCount,
          pendingCount);

    } catch (Exception e) {
      log.error("Error during promotion statistics logging", e);
    }
  }
}
