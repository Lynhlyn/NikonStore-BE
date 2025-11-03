package com.example.nikonbe.api.client.feature;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.feature.dto.response.FeatureResponseDTO;
import com.example.nikonbe.modules.feature.service.interF.FeatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/features")
@RequiredArgsConstructor
@Tag(name = "Client - Feature API", description = "API tính năng dành cho người dùng")
public class FeatureController {

  private final FeatureService featureService;

  @GetMapping
  @Operation(
      summary = "Lấy danh sách tính năng",
      description = "Lấy tất cả tính năng với bộ lọc tùy chọn")
  @ApiResponse(responseCode = "200", description = "Lấy thành công")
  public ResponseEntity<ApiResponseDto<List<FeatureResponseDTO>>> getAll(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String featureGroup) {
    List<FeatureResponseDTO> result = featureService.getAll(name, featureGroup);
    return ResponseUtils.success(result, "Features retrieved successfully");
  }
}
