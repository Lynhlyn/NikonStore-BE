package com.example.nikonbe.api.client.promotion;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.promotion.dto.response.PromotionResponseDTO;
import com.example.nikonbe.modules.promotion.service.interF.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/promotions")
@RequiredArgsConstructor
@Tag(name = "Client - Promotion", description = "Các API khuyến mãi dành cho khách hàng")
public class PromotionController {

  private final PromotionService promotionService;

  @GetMapping("/active")
  @Operation(
      summary = "Lấy danh sách khuyến mãi đang hoạt động",
      description = "Lấy tất cả các khuyến mãi đang có hiệu lực (status = ACTIVE và trong thời gian áp dụng)")
  @ApiResponse(responseCode = "200", description = "Lấy danh sách khuyến mãi thành công")
  public ResponseEntity<ApiResponseDto<List<PromotionResponseDTO>>> getActivePromotions() {
    List<PromotionResponseDTO> promotions = promotionService.getActivePromotions();
    return ResponseUtils.success(promotions, "Lấy danh sách khuyến mãi đang hoạt động thành công");
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Lấy chi tiết khuyến mãi",
      description = "Lấy thông tin chi tiết của một khuyến mãi theo ID")
  @ApiResponse(responseCode = "200", description = "Lấy chi tiết khuyến mãi thành công")
  public ResponseEntity<ApiResponseDto<PromotionResponseDTO>> getPromotionById(
      @Parameter(description = "ID khuyến mãi") @PathVariable Integer id) {
    PromotionResponseDTO promotion = promotionService.getPromotionByIdWithDetails(id);
    return ResponseUtils.success(promotion, "Lấy chi tiết khuyến mãi thành công");
  }

  @GetMapping("/code/{code}")
  @Operation(
      summary = "Lấy khuyến mãi theo mã",
      description = "Lấy thông tin khuyến mãi theo mã khuyến mãi")
  @ApiResponse(responseCode = "200", description = "Lấy khuyến mãi thành công")
  public ResponseEntity<ApiResponseDto<PromotionResponseDTO>> getPromotionByCode(
      @Parameter(description = "Mã khuyến mãi") @PathVariable String code) {
    PromotionResponseDTO promotion = promotionService.getPromotionByCode(code);
    return ResponseUtils.success(promotion, "Lấy khuyến mãi thành công");
  }
}
