package com.example.nikonbe.api.client.product_tag;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.product_tag.dto.response.ProductTagResponseDTO;
import com.example.nikonbe.modules.product_tag.service.interF.ProductTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/product-tags")
@RequiredArgsConstructor
@Tag(name = "Client - Product Tag API", description = "API thẻ sản phẩm dành cho người dùng")
public class ProductTagController {

  private final ProductTagService productTagService;

  @GetMapping("/tags/{tagId}")
  @Operation(summary = "Lấy danh sách sản phẩm theo thẻ")
  @ApiResponse(responseCode = "200", description = "Lấy thành công")
  public ResponseEntity<ApiResponseDto<List<ProductTagResponseDTO>>> getByTagId(
      @Parameter(description = "ID thẻ") @PathVariable Integer tagId) {
    List<ProductTagResponseDTO> result = productTagService.getByTagId(tagId);
    return ResponseUtils.success(result, "Products with tag retrieved successfully");
  }
}
