package com.example.nikonbe.api.client.attributes.brand;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.attributes.brand.dto.response.BrandResponseDTO;
import com.example.nikonbe.modules.attributes.brand.service.interF.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/brands")
@RequiredArgsConstructor
@Tag(name = "Client - Brand API", description = "Các API thương hiệu dành cho người dùng")
public class BrandController {

  private final BrandService brandService;

  @GetMapping
  @Operation(
      summary = "Lấy danh sách thương hiệu ACTIVE",
      description = "Có thể lấy tất cả hoặc phân trang")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<BrandResponseDTO>>> getActiveBrands(
      @Parameter(description = "Lấy tất cả không phân trang") @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "name") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {

    if (isAll) {
      List<BrandResponseDTO> result = brandService.getAllByStatus(Status.ACTIVE);
      return ResponseUtils.success(result, "Brands retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<BrandResponseDTO> result = brandService.getAllByStatusPaginated(Status.ACTIVE, pageable);

    return ResponseUtils.successWithPage(result, "Brands retrieved successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy thương hiệu ACTIVE theo ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy thương hiệu ACTIVE")
  })
  public ResponseEntity<ApiResponseDto<BrandResponseDTO>> getActiveById(
      @Parameter(description = "ID thương hiệu") @PathVariable Integer id) {
    BrandResponseDTO brand = brandService.getById(id);

    if (brand.getStatus() != Status.ACTIVE) {
      return ResponseUtils.error("Brand not found", HttpStatus.NOT_FOUND);
    }

    return ResponseUtils.success(brand, "Brand retrieved successfully");
  }
}
