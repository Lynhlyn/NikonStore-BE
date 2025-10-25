package com.example.nikonbe.api.client.attributes.color;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.attributes.color.dto.response.ColorResponseDTO;
import com.example.nikonbe.modules.attributes.color.service.interF.ColorService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/colors")
@RequiredArgsConstructor
@Tag(name = "Client - Color API", description = "Các API màu sắc dành cho người dùng")
public class ColorController {

  private final ColorService colorService;

  @GetMapping
  @Operation(
      summary = "Lấy danh sách tất cả màu sắc",
      description = "Có thể lấy tất cả hoặc phân trang, lọc theo tên và trạng thái")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<ColorResponseDTO>>> getColors(
      @Parameter(description = "Lấy tất cả không phân trang") @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Tìm kiếm theo tên") @RequestParam(required = false) String name,
      @Parameter(description = "Lọc theo trạng thái") @RequestParam(required = false) Status status,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "name") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "asc")
          String direction) {

    if (isAll) {
      List<ColorResponseDTO> result = colorService.getAll(name, status);
      return ResponseUtils.success(result, "Colors retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<ColorResponseDTO> result = colorService.getAllPaginated(name, status, pageable);

    return ResponseUtils.successWithPage(result, "Colors retrieved successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy màu sắc theo ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy màu sắc")
  })
  public ResponseEntity<ApiResponseDto<ColorResponseDTO>> getById(
      @Parameter(description = "ID của màu sắc") @PathVariable Integer id) {
    ColorResponseDTO result = colorService.getById(id);
    return ResponseUtils.success(result, "Color retrieved successfully");
  }
}
