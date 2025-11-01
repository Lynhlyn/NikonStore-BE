package com.example.nikonbe.api.client.cart;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.modules.cart.dto.request.AddToCartRequest;
import com.example.nikonbe.modules.cart.dto.request.DeleteCartItemRequest;
import com.example.nikonbe.modules.cart.dto.request.GetCartRequest;
import com.example.nikonbe.modules.cart.dto.request.UpdateCartItemRequest;
import com.example.nikonbe.modules.cart.dto.response.CartResponse;
import com.example.nikonbe.modules.cart.service.interF.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/carts")
@RequiredArgsConstructor
@Tag(name = "Client - Cart API", description = "API giỏ hàng dành cho khách hàng")
public class CartController {

  private final CartService cartService;

  @PostMapping("/fetch")
  @Operation(
      summary = "Lấy thông tin giỏ hàng",
      description = "Lấy thông tin giỏ hàng dựa trên customerId hoặc cookieId")
  public ResponseEntity<ApiResponseDto<CartResponse>> getCart(
      @Parameter(description = "Lấy thông tin giỏ hàng dựa trên customerId hoặc cookieId")
          @Valid
          @RequestBody
          GetCartRequest request) {
    CartResponse cartResponse = cartService.getCart(request);
    return ResponseEntity.ok(
        ApiResponseDto.<CartResponse>builder()
            .status(HttpStatus.OK.value())
            .message("Lấy giỏ hàng thành công")
            .data(cartResponse)
            .build());
  }

  @PostMapping
  @Operation(
      summary = "Thêm sản phẩm vào giỏ hàng",
      description = "Thêm sản phẩm vào giỏ hàng của khách hàng hoặc khách vãng lai")
  public ResponseEntity<ApiResponseDto<CartResponse>> addToCart(
      @Parameter(description = "Thông tin sản phẩm cần thêm vào giỏ hàng") @Valid @RequestBody
          AddToCartRequest request) {
    CartResponse cartResponse = cartService.addToCart(request);
    return ResponseEntity.ok(
        ApiResponseDto.<CartResponse>builder()
            .status(HttpStatus.OK.value())
            .message("Thêm sản phẩm vào giỏ hàng thành công")
            .data(cartResponse)
            .build());
  }

  @PutMapping
  @Operation(
      summary = "Cập nhật sản phẩm trong giỏ hàng",
      description = "Cập nhật số lượng của một sản phẩm trong giỏ hàng")
  public ResponseEntity<ApiResponseDto<CartResponse>> updateCartItem(
      @Parameter(description = "Thông tin sản phẩm cần cập nhật trong giỏ hàng") @Valid @RequestBody
          UpdateCartItemRequest request) {
    CartResponse cartResponse = cartService.updateCartItem(request);
    return ResponseEntity.ok(
        ApiResponseDto.<CartResponse>builder()
            .status(HttpStatus.OK.value())
            .message("Cập nhật giỏ hàng thành công")
            .data(cartResponse)
            .build());
  }

  @DeleteMapping
  @Operation(
      summary = "Xóa sản phẩm khỏi giỏ hàng",
      description = "Xóa một sản phẩm khỏi giỏ hàng của khách hàng hoặc khách vãng lai")
  public ResponseEntity<ApiResponseDto<CartResponse>> deleteCartItem(
      @Parameter(description = "Thông tin sản phẩm cần xóa khỏi giỏ hàng") @Valid @RequestBody
          DeleteCartItemRequest request) {
    CartResponse cartResponse = cartService.deleteCartItem(request);
    return ResponseEntity.ok(
        ApiResponseDto.<CartResponse>builder()
            .status(HttpStatus.OK.value())
            .message("Xóa sản phẩm khỏi giỏ hàng thành công")
            .data(cartResponse)
            .build());
  }

  @PostMapping("/assign")
  @Operation(
      summary = "Gán giỏ hàng khách vãng lai vào tài khoản",
      description = "Gán giỏ hàng của cookieId vào tài khoản khách hàng khi đăng nhập hoặc đăng ký")
  public ResponseEntity<ApiResponseDto<CartResponse>> assignCartToCustomer(
      @Parameter(description = "ID của khách hàng") @RequestParam Integer customerId,
      @Parameter(description = "Cookie ID của giỏ hàng khách vãng lai") @RequestParam
          String cookieId) {
    CartResponse cartResponse = cartService.assignCartToCustomer(customerId, cookieId);
    return ResponseEntity.ok(
        ApiResponseDto.<CartResponse>builder()
            .status(HttpStatus.OK.value())
            .message("Gán giỏ hàng vào tài khoản thành công")
            .data(cartResponse)
            .build());
  }
}
