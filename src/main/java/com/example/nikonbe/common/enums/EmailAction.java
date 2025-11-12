package com.example.nikonbe.common.enums;

public enum EmailAction {
  REGISTER_SUCCESS("register_success", "Đăng ký thành công"),
  FORGOT_PASSWORD("forgot_password", "Quên mật khẩu"),
  RESET_PASSWORD("reset_password", "Đặt lại mật khẩu"),
  PASSWORD_RESET("password_reset", "Đặt lại mật khẩu"),
  PASSWORD_CHANGED("password_changed", "Mật khẩu đã được thay đổi"),
  VERIFY_EMAIL("verify_email", "Xác thực email"),
  WELCOME("welcome", "Chào mừng"),
  ORDER_PENDING_CONFIRMATION("order_pending_confirmation", "Đơn hàng chờ xác nhận"),
  ORDER_CONFIRMATION("order_confirmation", "Xác nhận đơn hàng"),
  ORDER_CONFIRM("order_confirm", "Xác nhận đơn hàng"),
  CONFIRMATION("confirmation", "Xác nhận"),
  ORDER_CONFIRMED("order_confirmed", "Đơn hàng đã xác nhận"),
  ORDER_PREPARING("order_preparing", "Đơn hàng đang chuẩn bị"),
  ORDER_SHIPPING("order_shipping", "Đơn hàng đang giao"),
  ORDER_COMPLETED("order_completed", "Đơn hàng hoàn thành"),
  ORDER_CANCELLED("order_cancelled", "Đơn hàng đã hủy"),
  ORDER_PENDING_PAYMENT("order_pending_payment", "Đơn hàng chờ thanh toán"),
  ORDER_FAILED_DELIVERY("order_failed_delivery", "Đơn hàng giao thất bại"),
  ACCOUNT_LOCKED("account_locked", "Tài khoản bị khóa"),
  ACCOUNT_UNLOCKED("account_unlocked", "Tài khoản được mở khóa"),
  ACCOUNT_DISABLED("account_disabled", "Tài khoản bị vô hiệu hóa"),
  PROMOTION("promotion", "Khuyến mãi"),
  NEWSLETTER("newsletter", "Bản tin"),
  BRANDSNEW("brands_new", "Thương hiệu mới"),
  AUTHENTICATION_CODE("authentication_code", "Mã xác thực"),
  VOUCHER_ASSIGNED("voucher_assigned", "Voucher được gán");

  private final String value;
  private final String description;

  EmailAction(String value, String description) {
    this.value = value;
    this.description = description;
  }

  public String getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }

  public static EmailAction fromValue(String value) {
    for (EmailAction action : EmailAction.values()) {
      if (action.getValue().equalsIgnoreCase(value)) {
        return action;
      }
    }
    throw new IllegalArgumentException("Unknown email action value: " + value);
  }

  public static boolean isValidValue(String value) {
    try {
      fromValue(value);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
