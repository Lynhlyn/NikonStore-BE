package com.example.nikonbe.api.admin.promotion;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.promotion.dto.request.PromotionCreateDTO;
import com.example.nikonbe.modules.promotion.dto.request.PromotionSearchDTO;
import com.example.nikonbe.modules.promotion.dto.request.PromotionUpdateDTO;
import com.example.nikonbe.modules.promotion.dto.response.PromotionResponseDTO;
import com.example.nikonbe.modules.promotion.service.interF.PromotionService;
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
@RequestMapping("${api.admin.version}/promotions")
@RequiredArgsConstructor
@Tag(name = "Admin - Promotion Management", description = "Các API quản lý khuyến mãi cho admin")
public class PromotionAdminController {

  private final PromotionService promotionService;

  @PostMapping
  @Operation(summary = "Tạo mới khuyến mãi")
  @ApiResponse(
      responseCode = "201",
      description = "Tạo thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<PromotionResponseDTO>> create(
      @Valid @RequestBody PromotionCreateDTO dto) {
    PromotionResponseDTO result = promotionService.createPromotion(dto);
    return ResponseUtils.success(result, "Promotion created successfully", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật khuyến mãi")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Cập nhật thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khuyến mãi")
  })
  public ResponseEntity<ApiResponseDto<PromotionResponseDTO>> update(
      @Parameter(description = "ID khuyến mãi") @PathVariable Integer id,
      @Valid @RequestBody PromotionUpdateDTO dto) {
    PromotionResponseDTO result = promotionService.updatePromotion(id, dto);
    return ResponseUtils.success(result, "Promotion updated successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy khuyến mãi theo ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khuyến mãi")
  })
  public ResponseEntity<ApiResponseDto<PromotionResponseDTO>> getById(
      @Parameter(description = "ID khuyến mãi") @PathVariable Integer id) {
    PromotionResponseDTO result = promotionService.getPromotionByIdWithDetails(id);
    return ResponseUtils.success(result, "Promotion retrieved successfully");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách khuyến mãi có phân trang",
      description = "Hỗ trợ phân trang và lọc theo trạng thái")
  @ApiResponse(responseCode = "200", description = "Lấy thành công")
  public ResponseEntity<ApiResponseDto<List<PromotionResponseDTO>>> getAll(
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<PromotionResponseDTO> result = promotionService.getAllPaginated(pageable);

    return ResponseUtils.successWithPage(result, "Promotions retrieved successfully");
  }

  @PostMapping("/search")
  @Operation(
      summary = "Tìm kiếm khuyến mãi",
      description = "Tìm kiếm khuyến mãi với các điều kiện lọc và phân trang")
  @ApiResponse(responseCode = "200", description = "Tìm kiếm thành công")
  public ResponseEntity<ApiResponseDto<List<PromotionResponseDTO>>> search(
      @Valid @RequestBody PromotionSearchDTO searchRequest) {
    Page<PromotionResponseDTO> result = promotionService.searchPromotions(searchRequest);
    return ResponseUtils.successWithPage(result, "Promotions searched successfully");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa khuyến mãi", description = "Đánh dấu khuyến mãi là DELETED")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Xóa thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khuyến mãi")
  })
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID khuyến mãi") @PathVariable Integer id) {
    promotionService.delete(id);
    return ResponseUtils.success(null, "Promotion deleted successfully");
  }

  @PutMapping("/{id}/toggle-status")
  @Operation(summary = "Bật/tắt trạng thái khuyến mãi")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Chuyển đổi trạng thái thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy khuyến mãi")
  })
  public ResponseEntity<ApiResponseDto<PromotionResponseDTO>> toggleStatus(
      @Parameter(description = "ID khuyến mãi") @PathVariable Integer id) {
    PromotionResponseDTO result = promotionService.toggleStatus(id);
    return ResponseUtils.success(result, "Promotion status toggled successfully");
  }
}
