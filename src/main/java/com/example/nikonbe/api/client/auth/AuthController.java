package com.example.nikonbe.api.client.auth;

import com.example.nikonbe.common.constants.AuthConstants;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.customer.dto.request.CustomerCreateDTO;
import com.example.nikonbe.modules.customer.dto.response.CustomerResponseDTO;
import com.example.nikonbe.modules.customer.service.interF.CustomerService;
import com.example.nikonbe.security.dto.request.LoginRequest;
import com.example.nikonbe.security.dto.response.MessageResponse;
import com.example.nikonbe.security.dto.response.TokenResponse;
import com.example.nikonbe.security.service.auth.CustomerAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/client/auth")
@RequiredArgsConstructor
@Tag(name = "Client - Authentication Account", description = "Các API bảo mật (customer)")
public class AuthController {

  private final CustomerAuthService customerAuthService;
  private final CustomerService customerService;

  /**
   * Đăng nhập vào hệ thống
   *
   * @param request Thông tin đăng nhập
   * @return trả về token - reToken
   */
  @PostMapping("/login")
  @Operation(summary = "Đăng nhập")
  @ApiResponse(responseCode = "200", description = "Đăng nhập thành công")
  public ResponseEntity<?> loginCustomer(@RequestBody @Valid LoginRequest request) {
    return customerAuthService.login(request);
  }

  /**
   * Tạo tài khoản mới
   *
   * @param dto Thông tin tạo tài khoản mới
   * @return Thông tin tài khoản vừa tạo + phản hồi thành công
   */
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

  /**
   * Làm mới token
   *
   * @param req Chuỗi làm mới token
   * @return trả về thông tin token mới
   */
  @PostMapping("/refresh-token")
  @Operation(summary = "Làm mới token")
  @ApiResponse(responseCode = "200", description = "Làm mới thành công")
  public ResponseEntity<TokenResponse> refreshToken(@RequestBody Map<String, String> req) {
    return ResponseEntity.ok(customerAuthService.refreshToken(req.get("refresh_token")));
  }

  /**
   * Endpoint để FE gọi kiểm tra access token còn hợp lệ không. Nếu token hợp lệ trả về 200, nếu hết
   * hạn filter sẽ trả 401 trước khi vào đây.
   */
  @GetMapping("/validate")
  @Operation(summary = "Validate access token")
  public ResponseEntity<MessageResponse> validateToken() {
    return ResponseEntity.ok(new MessageResponse(AuthConstants.TOKEN_VALID));
  }
}
