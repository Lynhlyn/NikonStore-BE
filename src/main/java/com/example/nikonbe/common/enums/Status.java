package com.example.nikonbe.common.enums;

public enum Status {
  INACTIVE(0), // Không hoạt động/Ẩn/Khóa (brand, category, customer, staff, v.v.)
  ACTIVE(1), // Hoạt động/Hiển thị/Còn hàng (brand, category, customer, product_detail, v.v.)
  DELETED(2), // Đã xóa (brand, category, tag, v.v.)
  PENDING_CONFIRMATION(3), // Chờ xác nhận (orders)
  CONFIRMED(4), // Đã xác nhận (orders)
  SHIPPING(5), // Đang giao (orders)
  COMPLETED(6), // Hoàn thành (orders)
  CANCELLED(7), // Đã hủy (orders)
  PENDING_PAYMENT(8), // Chờ thanh toán (orders)
  PRIVATE(9), // Riêng tư
  PUBLIC(10), // Công khai
  BLOCKED(11), // Bị chặn (customer, staff)
  FAILED_DELIVERY(12), // Giao hàng thất bại (orders)
  PREPARING(13), // Đang chuẩn bị hàng (orders)
  PENDING_START(14);
  private final int value;

  Status(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static Status fromValue(int value) {
    for (Status status : Status.values()) {
      if (status.getValue() == value) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown status value: " + value);
  }
}
