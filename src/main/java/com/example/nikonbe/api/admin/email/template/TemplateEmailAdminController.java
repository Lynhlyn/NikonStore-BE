package com.example.nikonbe.api.admin.email.template;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.email.template.dto.request.TemplateEmailCreateDTO;
import com.example.nikonbe.modules.email.template.dto.request.TemplateEmailUpdateDTO;
import com.example.nikonbe.modules.email.template.dto.response.TemplateEmailResponseDTO;
import com.example.nikonbe.modules.email.template.service.interF.TemplateEmailService;
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
@RequestMapping("${api.admin.version}/template-emails")
@RequiredArgsConstructor
@Tag(
    name = "Admin - Template Email Management",
    description = "Các API quản lý template email cho admin")
public class TemplateEmailAdminController {

  private final TemplateEmailService templateEmailService;

  @PostMapping
  @Operation(summary = "Tạo mới template email")
  @ApiResponse(
      responseCode = "201",
      description = "Tạo thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<TemplateEmailResponseDTO>> create(
      @Valid @RequestBody TemplateEmailCreateDTO dto) {
    TemplateEmailResponseDTO result = templateEmailService.create(dto);
    return ResponseUtils.success(result, "Template email created successfully", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật template email")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Cập nhật thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy template email")
  })
  public ResponseEntity<ApiResponseDto<TemplateEmailResponseDTO>> update(
      @Parameter(description = "ID template email") @PathVariable Integer id,
      @Valid @RequestBody TemplateEmailUpdateDTO dto) {
    TemplateEmailResponseDTO result = templateEmailService.update(id, dto);
    return ResponseUtils.success(result, "Template email updated successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy template email theo ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy template email")
  })
  public ResponseEntity<ApiResponseDto<TemplateEmailResponseDTO>> getById(
      @Parameter(description = "ID template email") @PathVariable Integer id) {
    TemplateEmailResponseDTO result = templateEmailService.getById(id);
    return ResponseUtils.success(result, "Template email retrieved successfully");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách template email",
      description = "Có thể lấy tất cả hoặc phân trang.")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<TemplateEmailResponseDTO>>> getAll(
      @Parameter(description = "Lấy tất cả không phân trang") @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "asc")
          String direction) {

    if (isAll) {
      List<TemplateEmailResponseDTO> result = templateEmailService.getAll();
      return ResponseUtils.success(result, "Template emails retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<TemplateEmailResponseDTO> result = templateEmailService.getAllPaginated(pageable);
    return ResponseUtils.successWithPage(result, "Template emails retrieved successfully");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa template email", description = "Xóa template email theo ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Xóa thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy template email")
  })
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID template email") @PathVariable Integer id) {
    templateEmailService.delete(id);
    return ResponseUtils.success(null, "Template email deleted successfully");
  }
}
