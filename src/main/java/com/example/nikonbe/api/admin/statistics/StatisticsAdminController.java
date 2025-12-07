package com.example.nikonbe.api.admin.statistics;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.modules.statistics.dto.request.StatisticsFilterRequest;
import com.example.nikonbe.modules.statistics.dto.response.GeneralStatisticsResponse;
import com.example.nikonbe.modules.statistics.service.interF.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.admin.version}/statistics")
@Tag(name = "Admin-Statistics API", description = "API thống kê dành cho quản trị viên")
@RequiredArgsConstructor
public class StatisticsAdminController {

  private final StatisticsService statisticsService;

  @GetMapping("/general")
  @Operation(summary = "Thống kê tổng quan", description = "Lấy tất cả thống kê trong một API")
  public ResponseEntity<ApiResponseDto<GeneralStatisticsResponse>> getGeneralStatistics(
      @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fromDate,
      @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate toDate,
      @Parameter(description = "Năm") @RequestParam(required = false) Integer year,
      @Parameter(description = "Tháng (1-12)") @RequestParam(required = false) Integer month) {

    StatisticsFilterRequest filter =
        StatisticsFilterRequest.builder()
            .fromDate(fromDate)
            .toDate(toDate)
            .year(year)
            .month(month)
            .build();

    GeneralStatisticsResponse result = statisticsService.getGeneralStatistics(filter);

    ApiResponseDto<GeneralStatisticsResponse> response =
        ApiResponseDto.<GeneralStatisticsResponse>builder()
            .status(HttpStatus.OK.value())
            .message("Thống kê tổng quan được lấy thành công")
            .data(result)
            .build();

    return ResponseEntity.ok(response);
  }
}

