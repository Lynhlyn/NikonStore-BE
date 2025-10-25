package com.example.nikonbe.api.client.attributes.material;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.attributes.material.dto.response.MaterialResponseDTO;
import com.example.nikonbe.modules.attributes.material.service.interF.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/materials")
@RequiredArgsConstructor
@Tag(name = "Client - Material API", description = "Các API chất liệu dành cho người dùng")
public class MaterialController {

  private final MaterialService materialService;

  @GetMapping
  @Operation(
      summary = "Lấy danh sách chất liệu ACTIVE",
      description = "Có thể lấy tất cả hoặc phân trang, hỗ trợ tìm kiếm")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<MaterialResponseDTO>>> getActiveMaterials(
      @Parameter(description = "Lấy tất cả không phân trang") @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Tìm kiếm theo tên") @RequestParam(required = false) String keyword,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "name") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "asc")
          String direction) {

    if (isAll) {
      List<MaterialResponseDTO> result;
      if (keyword != null && !keyword.isEmpty()) {
        result = materialService.search(keyword);
      } else {
        result = materialService.getAllByStatus(Status.ACTIVE);
      }
      return ResponseUtils.success(result, "Materials retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<MaterialResponseDTO> result;
    if (keyword != null && !keyword.isEmpty()) {
      result = materialService.searchPaginated(keyword, pageable);
    } else {
      result = materialService.getAllByStatusPaginated(Status.ACTIVE, pageable);
    }
    return ResponseUtils.successWithPage(result, "Materials retrieved successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy chất liệu ACTIVE theo ID")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<MaterialResponseDTO>> getActiveById(
      @Parameter(description = "ID chất liệu") @PathVariable Integer id) {
    MaterialResponseDTO material = materialService.getById(id);
    if (material.getStatus() != Status.ACTIVE) {
      return ResponseUtils.error("Material not found", HttpStatus.NOT_FOUND);
    }
    return ResponseUtils.success(material, "Material retrieved successfully");
  }
}
