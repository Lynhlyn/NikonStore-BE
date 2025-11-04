package com.example.nikonbe.api.admin.product_tag;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.product_tag.dto.request.ProductTagCreateDTO;
import com.example.nikonbe.modules.product_tag.dto.request.ProductTagUpdateDTO;
import com.example.nikonbe.modules.product_tag.dto.response.ProductTagResponseDTO;
import com.example.nikonbe.modules.product_tag.service.interF.ProductTagService;
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
@RequestMapping("${api.admin.version}/products/{productId}/tags")
@RequiredArgsConstructor
@Tag(name = "Admin - Product Tag Management", description = "API quản lý thẻ sản phẩm")
public class ProductTagAdminController {

  private final ProductTagService productTagService;

  @PostMapping
  @Operation(summary = "Thêm thẻ cho sản phẩm")
  @ApiResponse(responseCode = "201", description = "Thêm thành công")
  public ResponseEntity<ApiResponseDto<ProductTagResponseDTO>> addTag(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId,
      @Valid @RequestBody ProductTagCreateDTO dto) {
    ProductTagResponseDTO result = productTagService.addTag(productId, dto);
    return ResponseUtils.success(result, "Product tag added successfully", HttpStatus.CREATED);
  }

  @PutMapping
  @Operation(summary = "Cập nhật danh sách thẻ cho sản phẩm")
  @ApiResponse(responseCode = "200", description = "Cập nhật thành công")
  public ResponseEntity<ApiResponseDto<List<ProductTagResponseDTO>>> updateTags(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId,
      @Valid @RequestBody ProductTagUpdateDTO dto) {
    List<ProductTagResponseDTO> result = productTagService.updateTags(productId, dto);
    return ResponseUtils.success(result, "Product tags updated successfully");
  }

  @GetMapping
  @Operation(summary = "Lấy danh sách thẻ của sản phẩm")
  @ApiResponse(responseCode = "200", description = "Lấy thành công")
  public ResponseEntity<ApiResponseDto<List<ProductTagResponseDTO>>> getTagsByProduct(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId) {
    List<ProductTagResponseDTO> result = productTagService.getByProductId(productId);
    return ResponseUtils.success(result, "Product tags retrieved successfully");
  }

  @DeleteMapping("/{tagId}")
  @Operation(summary = "Xóa thẻ khỏi sản phẩm")
  @ApiResponse(responseCode = "200", description = "Xóa thành công")
  public ResponseEntity<ApiResponseDto<Void>> removeTag(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId,
      @Parameter(description = "ID thẻ") @PathVariable Integer tagId) {
    productTagService.removeTag(productId, tagId);
    return ResponseUtils.success(null, "Product tag removed successfully");
  }

  @DeleteMapping
  @Operation(summary = "Xóa tất cả thẻ khỏi sản phẩm")
  @ApiResponse(responseCode = "200", description = "Xóa thành công")
  public ResponseEntity<ApiResponseDto<Void>> removeAllTags(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId) {
    productTagService.removeAllTags(productId);
    return ResponseUtils.success(null, "All product tags removed successfully");
  }
}
