package com.example.nikonbe.api.admin.color_image;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.color_image.dto.request.ColorImageCreateDTO;
import com.example.nikonbe.modules.color_image.dto.request.ColorImageUpdateDTO;
import com.example.nikonbe.modules.color_image.dto.response.ColorImageResponseDTO;
import com.example.nikonbe.modules.color_image.service.interF.ColorImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.admin.version}/color-images")
@RequiredArgsConstructor
@Tag(name = "Admin - Color Image Management")
public class ColorImageAdminController {

  private final ColorImageService service;

  @PostMapping
  @Operation(summary = "Tạo hình ảnh theo màu sắc")
  public ResponseEntity<ApiResponseDto<ColorImageResponseDTO>> create(
      @Valid @RequestBody ColorImageCreateDTO dto) {
    ColorImageResponseDTO result = service.create(dto);
    return ResponseUtils.success(
        result, "Tạo hình ảnh theo màu sắc thành công", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật hình ảnh theo màu sắc")
  public ResponseEntity<ApiResponseDto<ColorImageResponseDTO>> update(
      @PathVariable Integer id, @Valid @RequestBody ColorImageUpdateDTO dto) {
    ColorImageResponseDTO result = service.update(id, dto);
    return ResponseUtils.success(result, "Cập nhật hình ảnh theo màu sắc thành công");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy chi tiết hình ảnh theo màu sắc")
  public ResponseEntity<ApiResponseDto<ColorImageResponseDTO>> getById(@PathVariable Integer id) {
    ColorImageResponseDTO result = service.getById(id);
    return ResponseUtils.success(result, "Lấy hình ảnh theo màu sắc thành công");
  }

  @GetMapping
  @Operation(summary = "Lấy danh sách tất cả hình ảnh theo màu sắc")
  public ResponseEntity<ApiResponseDto<List<ColorImageResponseDTO>>> getAll() {
    List<ColorImageResponseDTO> result = service.getAll();
    return ResponseUtils.success(result, "Lấy danh sách hình ảnh theo màu sắc thành công");
  }

  @GetMapping("/product/{productId}")
  @Operation(summary = "Lấy danh sách hình ảnh của sản phẩm theo màu sắc")
  public ResponseEntity<ApiResponseDto<List<ColorImageResponseDTO>>> getByProductId(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId) {
    List<ColorImageResponseDTO> result = service.getByProductId(productId);
    return ResponseUtils.success(result, "Lấy danh sách hình ảnh theo màu sắc thành công");
  }

  @GetMapping("/product/{productId}/color/{colorId}")
  @Operation(summary = "Lấy hình ảnh của sản phẩm theo màu sắc cụ thể")
  public ResponseEntity<ApiResponseDto<ColorImageResponseDTO>> getByProductIdAndColorId(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId,
      @Parameter(description = "ID màu sắc") @PathVariable Integer colorId) {
    ColorImageResponseDTO result = service.getByProductIdAndColorId(productId, colorId);
    return ResponseUtils.success(result, "Lấy hình ảnh theo màu sắc thành công");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa hình ảnh theo màu sắc")
  public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Integer id) {
    service.delete(id);
    return ResponseUtils.success(null, "Xóa hình ảnh theo màu sắc thành công");
  }

  @DeleteMapping("/product/{productId}/color/{colorId}")
  @Operation(summary = "Xóa hình ảnh của sản phẩm theo màu sắc cụ thể")
  public ResponseEntity<ApiResponseDto<Void>> deleteByProductAndColor(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId,
      @Parameter(description = "ID màu sắc") @PathVariable Integer colorId) {
    service.deleteByProductAndColor(productId, colorId);
    return ResponseUtils.success(null, "Xóa hình ảnh theo màu sắc thành công");
  }
}
