package com.example.nikonbe.api.client.faq;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.faq.dto.response.FAQResponseDTO;
import com.example.nikonbe.modules.faq.service.interF.FAQService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/faqs")
@RequiredArgsConstructor
@Tag(name = "Client - FAQ")
public class FAQController {

  private final FAQService faqService;

  @GetMapping("/{id}")
  @Operation(summary = "Lấy chi tiết FAQ", description = "Lấy thông tin chi tiết một FAQ")
  public ResponseEntity<ApiResponseDto<FAQResponseDTO>> getById(
      @Parameter(description = "ID FAQ") @PathVariable Integer id) {
    FAQResponseDTO result = faqService.getById(id);
    return ResponseUtils.success(result, "Lấy FAQ thành công");
  }

  @GetMapping
  @Operation(
      summary = "Danh sách FAQ",
      description = "Lấy danh sách FAQ đang hoạt động với bộ lọc")
  public ResponseEntity<ApiResponseDto<java.util.List<FAQResponseDTO>>> getAll(
      @Parameter(description = "ID danh mục") @RequestParam(required = false) Integer categoryId,
      @Parameter(description = "ID tag") @RequestParam(required = false) Integer tagId,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Số lượng mỗi trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Trường sắp xếp") @RequestParam(defaultValue = "createdAt")
          String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {
    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<FAQResponseDTO> result =
        faqService.getAll(categoryId, tagId, true, pageable);
    return ResponseUtils.successWithPage(result, "Lấy danh sách FAQ thành công");
  }
}


