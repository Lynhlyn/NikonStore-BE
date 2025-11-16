package com.example.nikonbe.api.admin.pos;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.pos.service.interF.PosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("${api.admin.version}/pos/vnpay")
@RequiredArgsConstructor
@Tag(name = "Admin - POS VNPAY Callback", description = "API xử lý callback từ VNPAY cho POS")
public class VNPayCallbackController {

  private final PosService posService;

  @GetMapping("/callback")
  @Operation(
      summary = "Callback từ VNPAY sau khi thanh toán",
      description = "API nhận callback từ VNPAY và xử lý kết quả thanh toán")
  public ResponseEntity<ApiResponseDto<String>> vnpayCallback(
      HttpServletRequest request, @RequestParam Map<String, String> params) {
    try {
      Map<String, String> vnpParams = new HashMap<>();
      for (Map.Entry<String, String> entry : params.entrySet()) {
        vnpParams.put(entry.getKey(), entry.getValue());
      }

      posService.handleVnpayCallback(vnpParams);

      return ResponseUtils.success("Thanh toán thành công", "Xử lý callback thành công");
    } catch (Exception e) {
      log.error("Error handling VNPAY callback: {}", e.getMessage(), e);
      return ResponseUtils.error(
          "Xử lý callback thất bại: " + e.getMessage(),
          org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
