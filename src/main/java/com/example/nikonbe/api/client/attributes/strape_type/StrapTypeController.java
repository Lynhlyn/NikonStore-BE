package com.example.nikonbe.api.client.attributes.strape_type;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.attributes.strape_type.dto.response.StrapTypeResponseDTO;
import com.example.nikonbe.modules.attributes.strape_type.service.interF.StrapTypeService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/strap-types")
@RequiredArgsConstructor
@Tag(name = "Client - Strap Type API", description = "Các API loại dây đeo dành cho người dùng")
public class StrapTypeController {

  private final StrapTypeService strapTypeService;

  @GetMapping
  @Operation(
      summary = "Lấy danh sách loại dây đeo ACTIVE",
      description = "Có thể lấy tất cả hoặc phân trang, hỗ trợ tìm kiếm")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<StrapTypeResponseDTO>>> getActiveStrapTypes(
      @Parameter(description = "Lấy tất cả không phân trang") @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Tìm kiếm theo tên") @RequestParam(required = false) String keyword,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "name") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {

    if (isAll) {
      List<StrapTypeResponseDTO> result;
      if (keyword != null && !keyword.isEmpty()) {
        result = strapTypeService.search(keyword);
      } else {
        result = strapTypeService.getAllByStatus(Status.ACTIVE);
      }
      return ResponseUtils.success(result, "Strap types retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<StrapTypeResponseDTO> result;
    if (keyword != null && !keyword.isEmpty()) {
      result = strapTypeService.searchPaginated(keyword, pageable);
    } else {
      result = strapTypeService.getAllByStatusPaginated(Status.ACTIVE, pageable);
    }
    return ResponseUtils.successWithPage(result, "Strap types retrieved successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy loại dây đeo ACTIVE theo ID")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<StrapTypeResponseDTO>> getActiveById(
      @Parameter(description = "ID loại dây đeo") @PathVariable Integer id) {
    StrapTypeResponseDTO strapType = strapTypeService.getById(id);
    if (strapType.getStatus() != Status.ACTIVE) {
      return ResponseUtils.error(
          "Strap type not found", org.springframework.http.HttpStatus.NOT_FOUND);
    }
    return ResponseUtils.success(strapType, "Strap type retrieved successfully");
  }
}
