package com.example.nikonbe.api.admin.attributes.strape_type;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.attributes.strape_type.dto.request.StrapTypeCreateDTO;
import com.example.nikonbe.modules.attributes.strape_type.dto.request.StrapTypeUpdateDTO;
import com.example.nikonbe.modules.attributes.strape_type.dto.response.StrapTypeResponseDTO;
import com.example.nikonbe.modules.attributes.strape_type.service.interF.StrapTypeService;
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
@RequestMapping("${api.admin.version}/strap-types")
@RequiredArgsConstructor
@Tag(name = "Admin - Strap Type Management", description = "Các API quản lý loại dây đeo cho admin")
public class StrapTypeAdminController {

  private final StrapTypeService strapTypeService;

  @PostMapping
  @Operation(summary = "Tạo mới loại dây đeo")
  @ApiResponse(
      responseCode = "201",
      description = "Tạo thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<StrapTypeResponseDTO>> create(
      @Valid @RequestBody StrapTypeCreateDTO dto) {
    StrapTypeResponseDTO result = strapTypeService.create(dto);
    return ResponseUtils.success(result, "Strap type created successfully", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật loại dây đeo")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Cập nhật thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy loại dây đeo")
  })
  public ResponseEntity<ApiResponseDto<StrapTypeResponseDTO>> update(
      @Parameter(description = "ID loại dây đeo") @PathVariable Integer id,
      @Valid @RequestBody StrapTypeUpdateDTO dto) {
    StrapTypeResponseDTO result = strapTypeService.update(id, dto);
    return ResponseUtils.success(result, "Strap type updated successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy loại dây đeo theo ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy loại dây đeo")
  })
  public ResponseEntity<ApiResponseDto<StrapTypeResponseDTO>> getById(
      @Parameter(description = "ID loại dây đeo") @PathVariable Integer id) {
    StrapTypeResponseDTO result = strapTypeService.getById(id);
    return ResponseUtils.success(result, "Strap type retrieved successfully");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách loại dây đeo",
      description = "Có thể lấy tất cả hoặc phân trang. Có thể lọc theo trạng thái hoặc tìm kiếm.")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<StrapTypeResponseDTO>>> getAll(
      @Parameter(description = "Lấy tất cả không phân trang") @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Lọc theo trạng thái") @RequestParam(required = false) Status status,
      @Parameter(description = "Tìm kiếm theo tên") @RequestParam(required = false) String keyword,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {

    if (isAll) {
      List<StrapTypeResponseDTO> result;
      if (keyword != null && !keyword.isEmpty()) {
        result = strapTypeService.search(keyword);
      } else if (status != null) {
        result = strapTypeService.getAllByStatus(status);
      } else {
        result = strapTypeService.getAll();
      }
      return ResponseUtils.success(result, "Strap types retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<StrapTypeResponseDTO> result;
    if (keyword != null && !keyword.isEmpty()) {
      result = strapTypeService.searchPaginated(keyword, pageable);
    } else if (status != null) {
      result = strapTypeService.getAllByStatusPaginated(status, pageable);
    } else {
      result = strapTypeService.getAllPaginated(pageable);
    }
    return ResponseUtils.successWithPage(result, "Strap types retrieved successfully");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa loại dây đeo", description = "Đánh dấu loại dây đeo là DELETED")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Xóa thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy loại dây đeo")
  })
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID loại dây đeo") @PathVariable Integer id) {
    strapTypeService.delete(id);
    return ResponseUtils.success(null, "Strap type deleted successfully");
  }
}
