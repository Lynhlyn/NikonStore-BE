package com.example.nikonbe.api.admin.product_feature;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.product_feature.dto.request.ProductFeatureCreateDTO;
import com.example.nikonbe.modules.product_feature.dto.request.ProductFeatureUpdateDTO;
import com.example.nikonbe.modules.product_feature.dto.response.ProductFeatureResponseDTO;
import com.example.nikonbe.modules.product_feature.service.interF.ProductFeatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.admin.version}/products/{productId}/features")
@RequiredArgsConstructor
@Tag(name = "Admin - Product Feature Management", description = "API quản lý tính năng sản phẩm")
public class ProductFeatureAdminController {

  private final ProductFeatureService productFeatureService;

  @PostMapping
  @Operation(summary = "Thêm tính năng cho sản phẩm")
  @ApiResponse(responseCode = "201", description = "Thêm thành công")
  public ResponseEntity<ApiResponseDto<ProductFeatureResponseDTO>> addFeatureToProduct(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId,
      @Valid @RequestBody ProductFeatureCreateDTO dto) {
    ProductFeatureResponseDTO result = productFeatureService.addFeatureToProduct(productId, dto);
    return ResponseUtils.success(result, "Product feature added successfully", HttpStatus.CREATED);
  }

  @PutMapping
  @Operation(summary = "Cập nhật danh sách tính năng cho sản phẩm")
  @ApiResponse(responseCode = "200", description = "Cập nhật thành công")
  public ResponseEntity<ApiResponseDto<List<ProductFeatureResponseDTO>>> updateProductFeatures(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId,
      @Valid @RequestBody ProductFeatureUpdateDTO dto) {
    List<ProductFeatureResponseDTO> result =
        productFeatureService.updateProductFeatures(productId, dto);
    return ResponseUtils.success(result, "Product features updated successfully");
  }

  @GetMapping
  @Operation(summary = "Lấy danh sách tính năng của sản phẩm")
  @ApiResponse(responseCode = "200", description = "Lấy thành công")
  public ResponseEntity<ApiResponseDto<List<ProductFeatureResponseDTO>>> getProductFeatures(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId) {
    List<ProductFeatureResponseDTO> result = productFeatureService.getByProductId(productId);
    return ResponseUtils.success(result, "Product features retrieved successfully");
  }

  @DeleteMapping("/{featureId}")
  @Operation(summary = "Xóa tính năng khỏi sản phẩm")
  @ApiResponse(responseCode = "200", description = "Xóa thành công")
  public ResponseEntity<ApiResponseDto<Void>> removeFeatureFromProduct(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId,
      @Parameter(description = "ID tính năng") @PathVariable Integer featureId) {
    productFeatureService.removeFeatureFromProduct(productId, featureId);
    return ResponseUtils.success(null, "Product feature removed successfully");
  }

  @DeleteMapping
  @Operation(summary = "Xóa tất cả tính năng khỏi sản phẩm")
  @ApiResponse(responseCode = "200", description = "Xóa thành công")
  public ResponseEntity<ApiResponseDto<Void>> removeAllFeaturesFromProduct(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId) {
    productFeatureService.removeAllFeaturesFromProduct(productId);
    return ResponseUtils.success(null, "All product features removed successfully");
  }
}
