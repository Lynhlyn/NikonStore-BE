package com.example.nikonbe.api.admin.attributes.capacity;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.attributes.capacity.dto.request.CapacityCreateDTO;
import com.example.nikonbe.modules.attributes.capacity.dto.request.CapacityUpdateDTO;
import com.example.nikonbe.modules.attributes.capacity.dto.response.CapacityResponseDTO;
import com.example.nikonbe.modules.attributes.capacity.service.interF.CapacityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.admin.version}/capacities")
@RequiredArgsConstructor
@Tag(name = "Admin - Capacity Management", description = "Các API quản lý dung tích cho admin")
public class CapacityAdminController {

  private final CapacityService capacityService;

  @PostMapping
  @Operation(summary = "Tạo mới dung tích")
  @ApiResponse(
      responseCode = "201",
      description = "Tạo thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<CapacityResponseDTO>> create(
      @Valid @RequestBody CapacityCreateDTO dto) {
    CapacityResponseDTO result = capacityService.create(dto);
    return ResponseUtils.success(result, "Capacity created successfully", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật dung tích")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Cập nhật thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy dung tích")
  })
  public ResponseEntity<ApiResponseDto<CapacityResponseDTO>> update(
      @Parameter(description = "ID dung tích") @PathVariable Integer id,
      @Valid @RequestBody CapacityUpdateDTO dto) {
    CapacityResponseDTO result = capacityService.update(id, dto);
    return ResponseUtils.success(result, "Capacity updated successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy dung tích theo ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy dung tích")
  })
  public ResponseEntity<ApiResponseDto<CapacityResponseDTO>> getById(
      @Parameter(description = "ID dung tích") @PathVariable Integer id) {
    CapacityResponseDTO result = capacityService.getById(id);
    return ResponseUtils.success(result, "Capacity retrieved successfully");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách dung tích",
      description = "Có thể lấy tất cả hoặc phân trang. Có thể lọc theo trạng thái hoặc tìm kiếm.")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<CapacityResponseDTO>>> getAll(
      @Parameter(description = "Lấy tất cả không phân trang") @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Lọc theo trạng thái") @RequestParam(required = false) Status status,
      @Parameter(description = "Tìm kiếm theo tên") @RequestParam(required = false) String keyword,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "asc")
          String direction) {

    if (isAll) {
      List<CapacityResponseDTO> result;
      if (keyword != null && !keyword.isEmpty()) {
        result = capacityService.search(keyword);
      } else if (status != null) {
        result = capacityService.getAllByStatus(status);
      } else {
        result = capacityService.getAll();
      }
      return ResponseUtils.success(result, "Capacities retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<CapacityResponseDTO> result;
    if (keyword != null && !keyword.isEmpty()) {
      result = capacityService.searchPaginated(keyword, pageable);
    } else if (status != null) {
      result = capacityService.getAllByStatusPaginated(status, pageable);
    } else {
      result = capacityService.getAllPaginated(pageable);
    }
    return ResponseUtils.successWithPage(result, "Capacities retrieved successfully");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa dung tích", description = "Đánh dấu dung tích là DELETED")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Xóa thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy dung tích")
  })
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID dung tích") @PathVariable Integer id) {
    capacityService.delete(id);
    return ResponseUtils.success(null, "Capacity deleted successfully");
  }
}
