package com.example.nikonbe.api.admin.faq;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.faq.dto.request.FAQCreateDTO;
import com.example.nikonbe.modules.faq.dto.request.FAQUpdateDTO;
import com.example.nikonbe.modules.faq.dto.response.FAQResponseDTO;
import com.example.nikonbe.modules.faq.service.interF.FAQService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.admin.version}/faqs")
@RequiredArgsConstructor
@Tag(name = "Admin - FAQ Management")
public class FAQAdminController {

  private final FAQService faqService;

  @PostMapping
  @Operation(summary = "Tạo FAQ mới", description = "Admin tạo FAQ mới")
  public ResponseEntity<ApiResponseDto<FAQResponseDTO>> create(
      @Valid @RequestBody FAQCreateDTO dto) {
    FAQResponseDTO result = faqService.create(dto);
    return ResponseUtils.success(result, "Tạo FAQ thành công", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật FAQ", description = "Admin cập nhật FAQ")
  public ResponseEntity<ApiResponseDto<FAQResponseDTO>> update(
      @Parameter(description = "ID FAQ") @PathVariable Integer id,
      @Valid @RequestBody FAQUpdateDTO dto) {
    FAQResponseDTO result = faqService.update(id, dto);
    return ResponseUtils.success(result, "Cập nhật FAQ thành công");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy chi tiết FAQ", description = "Lấy thông tin chi tiết một FAQ")
  public ResponseEntity<ApiResponseDto<FAQResponseDTO>> getById(
      @Parameter(description = "ID FAQ") @PathVariable Integer id) {
    FAQResponseDTO result = faqService.getById(id);
    return ResponseUtils.success(result, "Lấy FAQ thành công");
  }

  @GetMapping
  @Operation(summary = "Danh sách FAQ", description = "Lấy danh sách FAQ với bộ lọc")
  public ResponseEntity<ApiResponseDto<java.util.List<FAQResponseDTO>>> getAll(
      @Parameter(description = "ID danh mục") @RequestParam(required = false) Integer categoryId,
      @Parameter(description = "ID tag") @RequestParam(required = false) Integer tagId,
      @Parameter(description = "Trạng thái") @RequestParam(required = false) Boolean status,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Số lượng mỗi trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Trường sắp xếp") @RequestParam(defaultValue = "createdAt")
          String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {
    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<FAQResponseDTO> result = faqService.getAll(categoryId, tagId, status, pageable);
    return ResponseUtils.successWithPage(result, "Lấy danh sách FAQ thành công");
  }

  @PutMapping("/{id}/status")
  @Operation(summary = "Cập nhật trạng thái FAQ", description = "Admin cập nhật trạng thái FAQ")
  public ResponseEntity<ApiResponseDto<FAQResponseDTO>> updateStatus(
      @Parameter(description = "ID FAQ") @PathVariable Integer id,
      @Parameter(description = "Trạng thái") @RequestParam Boolean status) {
    FAQResponseDTO result = faqService.updateStatus(id, status);
    return ResponseUtils.success(result, "Cập nhật trạng thái FAQ thành công");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa FAQ", description = "Admin xóa FAQ")
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID FAQ") @PathVariable Integer id) {
    faqService.delete(id);
    return ResponseUtils.success(null, "Xóa FAQ thành công");
  }
}


