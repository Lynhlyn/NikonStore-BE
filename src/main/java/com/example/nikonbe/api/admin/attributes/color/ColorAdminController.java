package com.example.nikonbe.api.admin.attributes.color;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.attributes.color.dto.request.ColorCreateDTO;
import com.example.nikonbe.modules.attributes.color.dto.request.ColorUpdateDTO;
import com.example.nikonbe.modules.attributes.color.dto.response.ColorResponseDTO;
import com.example.nikonbe.modules.attributes.color.service.interF.ColorService;
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
@RequestMapping("${api.admin.version}/colors")
@RequiredArgsConstructor
@Tag(name = "Admin - Color Management", description = "API quản lý màu sắc")
public class ColorAdminController {
  private final ColorService colorService;

  @PostMapping
  @Operation(summary = "Tạo mới màu sắc")
  @ApiResponse(
      responseCode = "201",
      description = "Tạo thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<ColorResponseDTO>> create(
      @Valid @RequestBody ColorCreateDTO dto) {
    ColorResponseDTO result = colorService.create(dto);
    return ResponseUtils.success(result, "Color created successfully", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật thông tin màu sắc")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Cập nhật thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy màu sắc")
  })
  public ResponseEntity<ApiResponseDto<ColorResponseDTO>> update(
      @Parameter(description = "ID màu sắc") @PathVariable Integer id,
      @Valid @RequestBody ColorUpdateDTO dto) {
    ColorResponseDTO result = colorService.update(id, dto);
    return ResponseUtils.success(result, "Color updated successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy thông tin màu sắc theo ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy màu sắc")
  })
  public ResponseEntity<ApiResponseDto<ColorResponseDTO>> getById(
      @Parameter(description = "ID màu sắc") @PathVariable Integer id) {
    ColorResponseDTO result = colorService.getById(id);
    return ResponseUtils.success(result, "Color retrieved successfully");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách màu sắc",
      description = "Có thể lấy tất cả hoặc phân trang, tìm kiếm theo tên và trạng thái.")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<ColorResponseDTO>>> getAll(
      @Parameter(description = "Lấy tất cả không phân trang") @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Tìm kiếm theo tên") @RequestParam(required = false) String name,
      @Parameter(description = "Lọc theo trạng thái") @RequestParam(required = false) Status status,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "name") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {

    if (isAll) {
      List<ColorResponseDTO> result = colorService.getAll(name, status);
      return ResponseUtils.success(result, "Colors retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<ColorResponseDTO> result = colorService.getAllPaginated(name, status, pageable);

    return ResponseUtils.successWithPage(result, "Colors retrieved successfully");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa màu sắc")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Xóa thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy màu sắc")
  })
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID màu sắc") @PathVariable Integer id) {
    colorService.delete(id);
    return ResponseUtils.success(null, "Color deleted successfully");
  }
}
