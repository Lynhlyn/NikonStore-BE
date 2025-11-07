package com.example.nikonbe.api.client.product_image;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.product_image.dto.response.ProductImageResponseDTO;
import com.example.nikonbe.modules.product_image.service.interF.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/v1/product-images")
@RequiredArgsConstructor
@Tag(name = "Client - Product Image")
public class ProductImageController {

  private final ProductImageService service;

  @GetMapping("/{id}")
  @Operation(summary = "Chi tiết hình ảnh sản phẩm")
  public ResponseEntity<ApiResponseDto<ProductImageResponseDTO>> getById(@PathVariable Integer id) {
    return ResponseUtils.success(service.getById(id), "Lấy hình ảnh sản phẩm thành công");
  }

  @GetMapping("/product/{productId}")
  @Operation(summary = "Danh sách hình ảnh của sản phẩm")
  public ResponseEntity<ApiResponseDto<List<ProductImageResponseDTO>>> getByProductId(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId) {
    return ResponseUtils.success(
        service.getByProductId(productId), "Lấy danh sách hình ảnh sản phẩm thành công");
  }
}
