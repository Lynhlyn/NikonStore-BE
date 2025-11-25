package com.example.nikonbe.modules.voucher.scheduler;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.voucher.entity.Voucher;
import com.example.nikonbe.modules.voucher.repository.VoucherRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Scheduler để tự động cập nhật trạng thái voucher Chạy mỗi 10 giây để đảm bảo voucher luôn có trạng
 * thái chính xác và tránh lạm dụng voucher quá hạn
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VoucherScheduler {

  private final VoucherRepository voucherRepository;

  /**
   * Tự động cập nhật trạng thái voucher mỗi 10 giây để tránh lạm dụng voucher quá hạn
   */
  @Scheduled(fixedRate = 10000) // Chạy mỗi 10 giây
  @Transactional
  public void updateVoucherStatus() {
    log.debug("Bắt đầu cập nhật trạng thái voucher tự động");

    LocalDateTime now = LocalDateTime.now();
    int updatedCount = 0;

    try {
      // 1. Cập nhật voucher chưa đến thời gian bắt đầu thành INACTIVE
      updatedCount += updatePendingVouchers(now);

      // 2. Cập nhật voucher đã hết hạn thành INACTIVE
      updatedCount += updateExpiredVouchers(now);

      // 3. Cập nhật voucher hết số lượng thành INACTIVE
      updatedCount += updateOutOfStockVouchers();

      // 4. Kích hoạt lại voucher đang trong thời gian sử dụng và còn số lượng
      updatedCount += activateAvailableVouchers(now);

      if (updatedCount > 0) {
        log.info("Đã cập nhật trạng thái cho {} voucher", updatedCount);
      }

    } catch (Exception e) {
      log.error("Lỗi khi cập nhật trạng thái voucher tự động: {}", e.getMessage(), e);
    }
  }

  /** Cập nhật voucher chưa đến thời gian bắt đầu thành PENDING_START */
  private int updatePendingVouchers(LocalDateTime now) {
    List<Voucher> pendingVouchers =
        voucherRepository.findAll().stream()
            .filter(v -> v.getStatus() == Status.ACTIVE && v.getStartDate().isAfter(now))
            .toList();

    if (!pendingVouchers.isEmpty()) {
      pendingVouchers.forEach(
          voucher -> {
            voucher.setStatus(Status.PENDING_START);
            log.debug(
                "Voucher {} chưa đến thời gian sử dụng, cập nhật thành PENDING_START",
                voucher.getCode());
          });
      voucherRepository.saveAll(pendingVouchers);
      return pendingVouchers.size();
    }
    return 0;
  }

  /** Cập nhật voucher đã hết hạn thành INACTIVE */
  private int updateExpiredVouchers(LocalDateTime now) {
    List<Voucher> expiredVouchers =
        voucherRepository.findExpiredVouchers(now).stream()
            .filter(v -> v.getStatus() == Status.ACTIVE || v.getStatus() == Status.PENDING_START)
            .toList();

    if (!expiredVouchers.isEmpty()) {
      expiredVouchers.forEach(
          voucher -> {
            voucher.setStatus(Status.INACTIVE);
            log.debug("Voucher {} đã hết hạn, cập nhật thành INACTIVE", voucher.getCode());
          });
      voucherRepository.saveAll(expiredVouchers);
      return expiredVouchers.size();
    }
    return 0;
  }

  /** Cập nhật voucher hết số lượng thành INACTIVE */
  private int updateOutOfStockVouchers() {
    List<Voucher> outOfStockVouchers =
        voucherRepository.findAll().stream()
            .filter(
                v -> {
                  // Kiểm tra hết số lượng
                  boolean outOfQuantity = v.getQuantity() <= 0;

                  return (v.getStatus() == Status.ACTIVE || v.getStatus() == Status.PENDING_START)
                      && outOfQuantity;
                })
            .toList();

    if (!outOfStockVouchers.isEmpty()) {
      outOfStockVouchers.forEach(
          voucher -> {
            voucher.setStatus(Status.INACTIVE);
            log.info("Voucher {} hết số lượng, cập nhật thành INACTIVE", voucher.getCode());
          });
      voucherRepository.saveAll(outOfStockVouchers);
      return outOfStockVouchers.size();
    }
    return 0;
  }

  /** Kích hoạt lại voucher từ PENDING_START thành ACTIVE khi đến thời gian */
  private int activateAvailableVouchers(LocalDateTime now) {
    List<Voucher> availableVouchers =
        voucherRepository.findAll().stream()
            .filter(
                v -> {
                  // CHỈ xử lý voucher có status PENDING_START (không cho phép từ INACTIVE)
                  if (v.getStatus() != Status.PENDING_START) {
                    return false;
                  }

                  // Kiểm tra đã đến thời gian bắt đầu
                  boolean hasStarted = !v.getStartDate().isAfter(now);

                  // Kiểm tra chưa hết hạn
                  boolean notExpired = !v.getEndDate().isBefore(now);

                  // Kiểm tra còn số lượng
                  boolean hasQuantity = v.getQuantity() > 0;

                  return hasStarted && notExpired && hasQuantity;
                })
            .toList();

    if (!availableVouchers.isEmpty()) {
      availableVouchers.forEach(
          voucher -> {
            // Chỉ chuyển từ PENDING_START thành ACTIVE
            voucher.setStatus(Status.ACTIVE);
            log.debug(
                "Voucher {} đã đến thời gian sử dụng, kích hoạt thành ACTIVE", voucher.getCode());
          });
      voucherRepository.saveAll(availableVouchers);
      return availableVouchers.size();
    }
    return 0;
  }

  /** Tạo báo cáo trạng thái voucher hàng ngày vào 8:00 AM */
  @Scheduled(cron = "0 0 8 * * *")
  @Transactional(readOnly = true)
  public void generateDailyVoucherReport() {
    try {
      LocalDateTime now = LocalDateTime.now();

      long totalVouchers = voucherRepository.count();
      long activeVouchers = voucherRepository.countActiveVouchers(now);
      long expiredVouchers = voucherRepository.findExpiredVouchers(now).size();

      // Voucher sắp hết hạn trong 7 ngày
      LocalDateTime nextWeek = now.plusDays(7);
      long expiringVouchers = voucherRepository.findVouchersExpiringWithin(now, nextWeek).size();

      log.info("=== BÁO CÁO VOUCHER HÀNG NGÀY ===");
      log.info("Tổng số voucher: {}", totalVouchers);
      log.info("Voucher đang hoạt động: {}", activeVouchers);
      log.info("Voucher đã hết hạn: {}", expiredVouchers);
      log.info("Voucher sắp hết hạn (7 ngày): {}", expiringVouchers);
      log.info("================================");

    } catch (Exception e) {
      log.error("Lỗi khi tạo báo cáo voucher hàng ngày: {}", e.getMessage(), e);
    }
  }

  /** Dọn dẹp voucher đã bị xóa lâu hơn 30 ngày - chạy hàng tuần vào Chủ nhật 2:00 AM */
  //    @Scheduled(cron = "0 0 2 * * SUN")
  //    @Transactional
  //    public void cleanupDeletedVouchers() {
  //        try {
  //            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
  //
  //            List<Voucher> vouchersToDelete = voucherRepository.findAll().stream()
  //                    .filter(v -> v.getStatus() == Status.DELETED
  //                            && v.getUpdatedAt() != null
  //                            && v.getUpdatedAt().isBefore(thirtyDaysAgo))
  //                    .toList();
  //
  //            if (!vouchersToDelete.isEmpty()) {
  //                voucherRepository.deleteAll(vouchersToDelete);
  //                log.info("Đã dọn dẹp {} voucher đã bị xóa lâu hơn 30 ngày",
  // vouchersToDelete.size());
  //            }
  //
  //        } catch (Exception e) {
  //            log.error("Lỗi khi dọn dẹp voucher đã xóa: {}", e.getMessage(), e);
  //        }
  //    }
}

