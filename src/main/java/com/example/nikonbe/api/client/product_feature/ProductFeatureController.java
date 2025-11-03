package com.example.nikonbe.api.client.product_feature;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.product_feature.dto.response.ProductFeatureResponseDTO;
import com.example.nikonbe.modules.product_feature.service.interF.ProductFeatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/products/{productId}/features")
@RequiredArgsConstructor
@Tag(
    name = "Client - Product Feature API",
    description = "API tính năng sản phẩm dành cho người dùng")
public class ProductFeatureController {

  private final ProductFeatureService productFeatureService;

  @GetMapping
  @Operation(summary = "Lấy danh sách tính năng của sản phẩm")
  @ApiResponse(responseCode = "200", description = "Lấy thành công")
  public ResponseEntity<ApiResponseDto<List<ProductFeatureResponseDTO>>> getProductFeatures(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId) {
    List<ProductFeatureResponseDTO> result = productFeatureService.getByProductId(productId);
    return ResponseUtils.success(result, "Product features retrieved successfully");
  }
}
