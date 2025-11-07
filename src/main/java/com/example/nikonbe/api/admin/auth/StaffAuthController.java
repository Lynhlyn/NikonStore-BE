package com.example.nikonbe.api.admin.auth;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.security.dto.request.AdminLoginRequest;
import com.example.nikonbe.security.dto.request.ForgotAdminPasswordRequest;
import com.example.nikonbe.security.dto.request.ResetPasswordRequest;
import com.example.nikonbe.security.dto.response.AdminLoginResponse;
import com.example.nikonbe.security.dto.response.CurrentUserResponse;
import com.example.nikonbe.security.dto.response.MessageResponse;
import com.example.nikonbe.security.dto.response.TokenResponse;
import com.example.nikonbe.security.service.auth.StaffAuthService;
import com.example.nikonbe.security.service.auth.StaffPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("${api.admin.version}/auth")
@RequiredArgsConstructor
@Tag(name = "Admin - Staff Authentication", description = "Các API xác thực cho staff admin")
public class StaffAuthController {

  private final StaffAuthService staffAuthService;
  private final StaffPasswordService staffPasswordService;

  @PostMapping("/login")
  @Operation(
      summary = "Đăng nhập admin",
      description = "Đăng nhập admin bằng email, password và role")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Đăng nhập thành công",
        content = @Content(schema = @Schema(implementation = AdminLoginResponse.class))),
    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
    @ApiResponse(responseCode = "401", description = "Sai thông tin đăng nhập"),
    @ApiResponse(responseCode = "403", description = "Tài khoản bị khóa hoặc không có quyền"),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản"),
    @ApiResponse(responseCode = "500", description = "Lỗi server")
  })
  public ResponseEntity<ApiResponseDto<AdminLoginResponse>> adminLogin(
      @Valid @RequestBody AdminLoginRequest request) {
    log.info("Admin login request for email: {}", request.getEmail());
    AdminLoginResponse response = staffAuthService.adminLogin(request);
    return ResponseUtils.success(response, "Đăng nhập thành công");
  }

  @PostMapping("/refresh-token")
  @Operation(
      summary = "Làm mới access token",
      description = "Sử dụng refresh token để tạo access token mới")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Làm mới token thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "400", description = "Refresh token không hợp lệ"),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy refresh token")
  })
  public ResponseEntity<ApiResponseDto<TokenResponse>> refreshToken(
      @RequestBody Map<String, String> request) {

    String refreshToken = request.get("refreshToken");
    TokenResponse result = staffAuthService.refreshToken(refreshToken);

    return ResponseUtils.success(result, "Làm mới token thành công");
  }

  @PostMapping("/logout")
  @Operation(summary = "Đăng xuất", description = "Đăng xuất và xóa tất cả token của staff")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Đăng xuất thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy staff")
  })
  public ResponseEntity<ApiResponseDto<Void>> logout(@RequestBody Map<String, String> request) {

    staffAuthService.logout(request);
    return ResponseUtils.success(null, "Đăng xuất thành công");
  }

  @PostMapping("/forgot-password")
  @Operation(summary = "Quên mật khẩu", description = "Gửi email chứa link đặt lại mật khẩu")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Gửi email thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "400", description = "Email không hợp lệ"),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy staff với email này")
  })
  public ResponseEntity<ApiResponseDto<MessageResponse>> forgotPassword(
      @Valid @RequestBody ForgotAdminPasswordRequest request) {

    log.info("Staff forgot password request for email: {}", request.getEmail());
    staffPasswordService.createPasswordResetToken(request.getEmail(), request.getRole());

    MessageResponse message = new MessageResponse("Email đặt lại mật khẩu đã được gửi");
    return ResponseUtils.success(message, "Gửi email thành công");
  }

  @PostMapping("/reset-password")
  @Operation(summary = "Đặt lại mật khẩu", description = "Đặt lại mật khẩu sử dụng token từ email")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Đặt lại mật khẩu thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "400", description = "Token không hợp lệ hoặc đã hết hạn"),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy staff")
  })
  public ResponseEntity<ApiResponseDto<MessageResponse>> resetPassword(
      @Valid @RequestBody ResetPasswordRequest request) {

    log.info("Staff reset password request");
    staffPasswordService.resetPassword(request.getToken(), request.getNewPassword());

    MessageResponse message = new MessageResponse("Đặt lại mật khẩu thành công");
    return ResponseUtils.success(message, "Đặt lại mật khẩu thành công");
  }

  @GetMapping("/validate-reset-token")
  @Operation(
      summary = "Kiểm tra token đặt lại mật khẩu",
      description = "Kiểm tra tính hợp lệ của token đặt lại mật khẩu")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Token hợp lệ",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "400", description = "Token không hợp lệ hoặc đã hết hạn")
  })
  public ResponseEntity<ApiResponseDto<MessageResponse>> validateResetToken(
      @RequestParam String token) {

    boolean isValid = staffPasswordService.validateResetToken(token);

    if (isValid) {
      MessageResponse message = new MessageResponse("Token hợp lệ");
      return ResponseUtils.success(message, "Token hợp lệ");
    } else {
      return ResponseUtils.error("Token không hợp lệ", HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/current-user")
  @Operation(
      summary = "Lấy thông tin nhân viên đang đăng nhập",
      description = "Lấy thông tin chi tiết của nhân viên đang đăng nhập dựa trên token")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thông tin thành công",
        content = @Content(schema = @Schema(implementation = CurrentUserResponse.class))),
    @ApiResponse(responseCode = "401", description = "Không có quyền truy cập"),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy nhân viên"),
    @ApiResponse(responseCode = "500", description = "Lỗi server")
  })
  public ResponseEntity<ApiResponseDto<CurrentUserResponse>> getCurrentUser(
      Authentication authentication) {

    if (authentication == null) {
      log.error("Authentication object is null");
      return ResponseUtils.error("Không có quyền truy cập", HttpStatus.UNAUTHORIZED);
    }

    log.info("Getting current user info for: {}", authentication.getName());
    log.debug(
        "Authentication details: authenticated={}, principal={}",
        authentication.isAuthenticated(),
        authentication.getPrincipal().getClass().getSimpleName());

    CurrentUserResponse response = staffAuthService.getCurrentUser(authentication);

    return ResponseUtils.success(response, "Lấy thông tin nhân viên thành công");
  }
}
