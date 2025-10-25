package com.example.nikonbe.api.admin.attributes.material;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.attributes.material.dto.request.MaterialCreateDTO;
import com.example.nikonbe.modules.attributes.material.dto.request.MaterialUpdateDTO;
import com.example.nikonbe.modules.attributes.material.dto.response.MaterialResponseDTO;
import com.example.nikonbe.modules.attributes.material.service.interF.MaterialService;
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
@RequestMapping("${api.admin.version}/materials")
@RequiredArgsConstructor
@Tag(name = "Admin - Material Management", description = "Các API quản lý chất liệu cho admin")
public class MaterialAdminController {

  private final MaterialService materialService;

  @PostMapping
  @Operation(summary = "Tạo mới chất liệu")
  @ApiResponse(
      responseCode = "201",
      description = "Tạo thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<MaterialResponseDTO>> create(
      @Valid @RequestBody MaterialCreateDTO dto) {
    MaterialResponseDTO result = materialService.create(dto);
    return ResponseUtils.success(result, "Material created successfully", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật chất liệu")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Cập nhật thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy chất liệu")
  })
  public ResponseEntity<ApiResponseDto<MaterialResponseDTO>> update(
      @Parameter(description = "ID chất liệu") @PathVariable Integer id,
      @Valid @RequestBody MaterialUpdateDTO dto) {
    MaterialResponseDTO result = materialService.update(id, dto);
    return ResponseUtils.success(result, "Material updated successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy chất liệu theo ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy chất liệu")
  })
  public ResponseEntity<ApiResponseDto<MaterialResponseDTO>> getById(
      @Parameter(description = "ID chất liệu") @PathVariable Integer id) {
    MaterialResponseDTO result = materialService.getById(id);
    return ResponseUtils.success(result, "Material retrieved successfully");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách chất liệu",
      description = "Có thể lấy tất cả hoặc phân trang. Có thể lọc theo trạng thái hoặc tìm kiếm.")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<MaterialResponseDTO>>> getAll(
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
      List<MaterialResponseDTO> result;
      if (keyword != null && !keyword.isEmpty()) {
        result = materialService.search(keyword);
      } else if (status != null) {
        result = materialService.getAllByStatus(status);
      } else {
        result = materialService.getAll();
      }
      return ResponseUtils.success(result, "Materials retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<MaterialResponseDTO> result;
    if (keyword != null && !keyword.isEmpty()) {
      result = materialService.searchPaginated(keyword, pageable);
    } else if (status != null) {
      result = materialService.getAllByStatusPaginated(status, pageable);
    } else {
      result = materialService.getAllPaginated(pageable);
    }
    return ResponseUtils.successWithPage(result, "Materials retrieved successfully");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa chất liệu", description = "Đánh dấu chất liệu là DELETED")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Xóa thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy chất liệu")
  })
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID chất liệu") @PathVariable Integer id) {
    materialService.delete(id);
    return ResponseUtils.success(null, "Material deleted successfully");
  }
}
