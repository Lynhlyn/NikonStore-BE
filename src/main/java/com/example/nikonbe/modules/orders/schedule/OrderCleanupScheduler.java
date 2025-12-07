package com.example.nikonbe.modules.orders.schedule;

import com.example.nikonbe.modules.pos.service.interF.PosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCleanupScheduler {

  private final PosService posService;

  @Scheduled(fixedRate = 1 * 60 * 1000)
  public void autoCancelOldPendingOrders() {
    try {
      log.info("Starting auto-cancel of old pending IN_STORE orders (over 1 hour)...");
      posService.cleanupOldPendingOrders();
      log.info("Auto-cancel of old pending IN_STORE orders completed successfully");
    } catch (Exception e) {
      log.error("Error during auto-cancel of old pending IN_STORE orders", e);
    }
  }
}

