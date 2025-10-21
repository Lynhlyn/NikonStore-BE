package com.example.nikonbe.api.client.attributes.capacity;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.attributes.capacity.dto.response.CapacityResponseDTO;
import com.example.nikonbe.modules.attributes.capacity.service.interF.CapacityService;
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
@RequestMapping("${api.version}/capacities")
@RequiredArgsConstructor
@Tag(name = "Client - Capacity API", description = "Các API dung tích dành cho người dùng")
public class CapacityController {

  private final CapacityService capacityService;

  @GetMapping
  @Operation(
      summary = "Lấy danh sách dung tích ACTIVE",
      description = "Có thể lấy tất cả hoặc phân trang, hỗ trợ tìm kiếm")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<CapacityResponseDTO>>> getActiveCapacities(
      @Parameter(description = "Lấy tất cả không phân trang") @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Tìm kiếm theo tên") @RequestParam(required = false) String keyword,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "name") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "asc")
          String direction) {

    if (isAll) {
      List<CapacityResponseDTO> result;
      if (keyword != null && !keyword.isEmpty()) {
        result = capacityService.search(keyword);
      } else {
        result = capacityService.getAllByStatus(Status.ACTIVE);
      }
      return ResponseUtils.success(result, "Capacities retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<CapacityResponseDTO> result;
    if (keyword != null && !keyword.isEmpty()) {
      result = capacityService.searchPaginated(keyword, pageable);
    } else {
      result = capacityService.getAllByStatusPaginated(Status.ACTIVE, pageable);
    }
    return ResponseUtils.successWithPage(result, "Capacities retrieved successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy dung tích ACTIVE theo ID")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<CapacityResponseDTO>> getActiveById(
      @Parameter(description = "ID dung tích") @PathVariable Integer id) {
    CapacityResponseDTO capacity = capacityService.getById(id);
    if (capacity.getStatus() != Status.ACTIVE) {
      return ResponseUtils.error(
          "Capacity not found", org.springframework.http.HttpStatus.NOT_FOUND);
    }
    return ResponseUtils.success(capacity, "Capacity retrieved successfully");
  }
}
