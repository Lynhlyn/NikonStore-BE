package com.example.nikonbe.api.client.banner;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.banner.dto.response.BannerResponseDTO;
import com.example.nikonbe.modules.banner.service.interF.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/banners")
@RequiredArgsConstructor
@Tag(name = "Client - Banner", description = "API lấy banner cho client")
public class BannerController {

  private final BannerService bannerService;

  @GetMapping
  @Operation(summary = "Lấy danh sách banner", description = "Lấy danh sách banner đang hoạt động")
  @ApiResponse(responseCode = "200", description = "Lấy danh sách banner thành công")
  public ResponseEntity<ApiResponseDto<List<BannerResponseDTO>>> getAllBanners(
      @Parameter(description = "Vị trí banner (0: top, 1: right, 2: bottom, 3: left)")
          @RequestParam(value = "position", required = false)
          Integer position) {

    List<BannerResponseDTO> result = bannerService.getAll(Status.ACTIVE, position);
    return ResponseUtils.success(result, "Lấy danh sách banner thành công");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy banner theo ID", description = "Lấy thông tin banner theo ID")
  @ApiResponse(responseCode = "200", description = "Lấy banner thành công")
  @ApiResponse(responseCode = "404", description = "Không tìm thấy banner")
  public ResponseEntity<ApiResponseDto<BannerResponseDTO>> getBannerById(
      @Parameter(description = "ID của banner") @PathVariable Long id) {

    BannerResponseDTO result = bannerService.getById(id);
    return ResponseUtils.success(result, "Lấy banner thành công");
  }

  @GetMapping("/position/{position}")
  @Operation(
      summary = "Lấy banner theo vị trí",
      description =
          "Lấy danh sách banner theo vị trí cụ thể (0: top, 1: right, 2: bottom, 3: left)")
  @ApiResponse(responseCode = "200", description = "Lấy banner theo vị trí thành công")
  public ResponseEntity<ApiResponseDto<List<BannerResponseDTO>>> getBannersByPosition(
      @Parameter(description = "Vị trí banner (0: top, 1: right, 2: bottom, 3: left)") @PathVariable
          Integer position) {

    List<BannerResponseDTO> result = bannerService.getActiveBannersByPosition(position);
    return ResponseUtils.success(result, "Lấy banner theo vị trí thành công");
  }
}
