package com.example.nikonbe.common.constants;

/** Constants for authentication and security operations */
public class AuthConstants {

  // Token expiration times
  // Đặt MODE_TEST_REFRESH=true để dùng REFRESH_TOKEN_VALIDITY_MINUTES_TEST thay vì DAYS
  public static final boolean MODE_TEST_REFRESH = false;
  public static final long ACCESS_TOKEN_VALIDITY_MINUTES = 30;
  public static final long REFRESH_TOKEN_VALIDITY_DAYS = 30;
  public static final long REFRESH_TOKEN_VALIDITY_DAYS_WITHOUT_REMEMBER = 1;
  public static final long REFRESH_TOKEN_VALIDITY_MINUTES_TEST = 30;
  public static final long RESET_TOKEN_VALIDITY_MINUTES = 15; // Password Reset Token: 15 phút

  // Validation constants
  public static final int MIN_PASSWORD_LENGTH = 8;
  public static final int MAX_PASSWORD_LENGTH = 32;
  public static final int MIN_AGE = 13;
  public static final int MAX_AGE = 100;
  public static final int MIN_PHONE_LENGTH = 10;
  public static final int MAX_PHONE_LENGTH = 11;

  // Error messages
  public static final String EMPTY_LOGIN_INFO = "Không để trống thông tin đăng nhập";
  public static final String INVALID_CREDENTIALS = "Tên người dùng hoặc mật khẩu không đúng";
  public static final String ACCOUNT_LOCKED = "Tài khoản đã bị khóa";
  public static final String ACCOUNT_NOT_FOUND = "Tài khoản không tồn tại";
  public static final String UNEXPECTED_ERROR = "Đã xảy ra lỗi không mong muốn";
  public static final String PASSWORD_RESET_EMAIL_SENT =
      "Email đặt lại mật khẩu đã được gửi thành công";
  public static final String PASSWORD_RESET_SUCCESS = "Đặt lại mật khẩu thành công";
  public static final String TOKEN_VALID = "Token hợp lệ";
  public static final String TOKEN_INVALID = "Token không hợp lệ hoặc đã hết hạn";
  public static final String LOGOUT_SUCCESS = "Logout successful.";

  // Roles
  public static final String ROLE_USER = "USER";
  public static final String ROLE_ADMIN = "ADMIN";

  private AuthConstants() {
    // Private constructor to prevent instantiation
  }
}
