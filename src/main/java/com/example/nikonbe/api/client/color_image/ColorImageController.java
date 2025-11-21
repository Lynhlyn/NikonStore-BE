package com.example.nikonbe.api.client.color_image;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.color_image.dto.response.ColorImageResponseDTO;
import com.example.nikonbe.modules.color_image.service.interF.ColorImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/color-images")
@RequiredArgsConstructor
@Tag(name = "Client - Color Image")
public class ColorImageController {

  private final ColorImageService service;

  @GetMapping("/{id}")
  @Operation(summary = "Chi tiết hình ảnh theo màu sắc")
  public ResponseEntity<ApiResponseDto<ColorImageResponseDTO>> getById(@PathVariable Integer id) {
    return ResponseUtils.success(service.getById(id), "Lấy hình ảnh theo màu sắc thành công");
  }

  @GetMapping("/product/{productId}")
  @Operation(summary = "Danh sách hình ảnh của sản phẩm theo màu sắc")
  public ResponseEntity<ApiResponseDto<List<ColorImageResponseDTO>>> getByProductId(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId) {
    return ResponseUtils.success(
        service.getByProductId(productId), "Lấy danh sách hình ảnh theo màu sắc thành công");
  }

  @GetMapping("/product/{productId}/color/{colorId}")
  @Operation(summary = "Hình ảnh của sản phẩm theo màu sắc cụ thể")
  public ResponseEntity<ApiResponseDto<ColorImageResponseDTO>> getByProductIdAndColorId(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer productId,
      @Parameter(description = "ID màu sắc") @PathVariable Integer colorId) {
    return ResponseUtils.success(
        service.getByProductIdAndColorId(productId, colorId),
        "Lấy hình ảnh theo màu sắc thành công");
  }
}
