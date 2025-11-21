package com.example.nikonbe.api.client.auth;

import com.example.nikonbe.common.constants.AuthConstants;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.customer.dto.request.CustomerCreateDTO;
import com.example.nikonbe.modules.customer.dto.response.CustomerResponseDTO;
import com.example.nikonbe.modules.customer.service.interF.CustomerService;
import com.example.nikonbe.security.dto.request.CustomerForgotPasswordRequest;
import com.example.nikonbe.security.dto.request.LoginRequest;
import com.example.nikonbe.security.dto.request.ResendVerificationEmailRequest;
import com.example.nikonbe.security.dto.request.ResetPasswordRequest;
import com.example.nikonbe.security.dto.response.MessageResponse;
import com.example.nikonbe.security.dto.response.TokenResponse;
import com.example.nikonbe.security.service.auth.CustomerAuthService;
import com.example.nikonbe.security.service.auth.CustomerPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("${api.version}/auth")
@RequiredArgsConstructor
@Tag(name = "Client - Authentication Account", description = "Các API bảo mật (customer)")
public class AuthController {

  private final CustomerAuthService customerAuthService;
  private final CustomerService customerService;
  private final CustomerPasswordService customerPasswordService;

  @PostMapping("/login")
  @Operation(summary = "Đăng nhập")
  @ApiResponse(responseCode = "200", description = "Đăng nhập thành công")
  public ResponseEntity<?> loginCustomer(@RequestBody @Valid LoginRequest request) {
    return customerAuthService.login(request);
  }

  @PostMapping(value = "/signup")
  @Operation(summary = "Tạo tài khoản mới.")
  @ApiResponse(responseCode = "201", description = "Tạo thành công")
  public ResponseEntity<ApiResponseDto<CustomerResponseDTO>> create(
      @Valid @RequestBody CustomerCreateDTO dto) throws IOException {
    CustomerResponseDTO result = customerService.create(dto);
    return ResponseUtils.success(result, "Tạo tài khoản thành công.", HttpStatus.CREATED);
  }

  @PostMapping("/logout")
  @Operation(summary = "Đăng xuất")
  @ApiResponse(responseCode = "200", description = "Đăng xuất thành công")
  public ResponseEntity<String> logout(@RequestBody Map<String, String> req) {
    customerAuthService.logout(req);
    return ResponseEntity.ok(AuthConstants.LOGOUT_SUCCESS);
  }

  @PostMapping("/refresh-token")
  @Operation(summary = "Làm mới token")
  @ApiResponse(responseCode = "200", description = "Làm mới thành công")
  public ResponseEntity<TokenResponse> refreshToken(@RequestBody Map<String, String> req) {
    return ResponseEntity.ok(customerAuthService.refreshToken(req.get("refresh_token")));
  }

  @GetMapping("/validate")
  @Operation(summary = "Validate access token")
  public ResponseEntity<MessageResponse> validateToken() {
    return ResponseEntity.ok(new MessageResponse(AuthConstants.TOKEN_VALID));
  }

  @PostMapping("/forgot-password")
  @Operation(summary = "Quên mật khẩu khách hàng")
  @ApiResponse(responseCode = "200", description = "Gửi email đặt lại mật khẩu thành công")
  public ResponseEntity<MessageResponse> forgotPassword(
      @Valid @RequestBody CustomerForgotPasswordRequest request) {
    customerPasswordService.requestPasswordReset(request.getEmail());
    return ResponseEntity.ok(new MessageResponse(AuthConstants.PASSWORD_RESET_EMAIL_SENT));
  }

  @PostMapping("/reset-password")
  @Operation(summary = "Đặt lại mật khẩu khách hàng")
  @ApiResponse(responseCode = "200", description = "Đặt lại mật khẩu thành công")
  public ResponseEntity<MessageResponse> resetPassword(
      @Valid @RequestBody ResetPasswordRequest request) {
    customerPasswordService.resetPassword(request.getToken(), request.getNewPassword());
    return ResponseEntity.ok(new MessageResponse("Đặt lại mật khẩu thành công"));
  }

  @GetMapping("/validate-reset-token")
  @Operation(summary = "Kiểm tra token đặt lại mật khẩu khách hàng")
  public ResponseEntity<MessageResponse> validateResetToken(@RequestParam String token) {
    boolean isValid = customerPasswordService.validateResetToken(token);
    if (isValid) {
      return ResponseEntity.ok(new MessageResponse("Token hợp lệ"));
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new MessageResponse("Token không hợp lệ hoặc đã hết hạn"));
  }

  @GetMapping("/verify-email")
  @Operation(summary = "Xác thực email khách hàng")
  public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token) {
    customerService.verifyEmail(token);
    return ResponseEntity.ok(new MessageResponse("Xác thực email thành công"));
  }

  @PostMapping("/resend-verification")
  @Operation(summary = "Gửi lại email xác thực khách hàng")
  public ResponseEntity<MessageResponse> resendVerification(
      @Valid @RequestBody ResendVerificationEmailRequest request) {
    customerService.resendVerificationEmail(request.getEmail());
    return ResponseEntity.ok(new MessageResponse("Đã gửi lại email xác thực"));
  }
}
