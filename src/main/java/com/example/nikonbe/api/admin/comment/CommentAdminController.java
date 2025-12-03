package com.example.nikonbe.api.admin.comment;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.comment.dto.request.CommentReplyDTO;
import com.example.nikonbe.modules.comment.dto.response.CommentResponseDTO;
import com.example.nikonbe.modules.comment.service.interF.CommentService;
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
@RequestMapping("${api.admin.version}/comments")
@RequiredArgsConstructor
@Tag(name = "Admin - Comment Management")
public class CommentAdminController {

  private final CommentService commentService;

  @PostMapping("/reply")
  @Operation(summary = "Trả lời comment", description = "Admin trả lời một comment")
  public ResponseEntity<ApiResponseDto<CommentResponseDTO>> reply(
      @Valid @RequestBody CommentReplyDTO dto) {
    CommentResponseDTO result = commentService.reply(dto);
    return ResponseUtils.success(result, "Trả lời comment thành công", HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy chi tiết comment", description = "Lấy thông tin chi tiết một comment")
  public ResponseEntity<ApiResponseDto<CommentResponseDTO>> getById(
      @Parameter(description = "ID comment") @PathVariable Integer id) {
    CommentResponseDTO result = commentService.getById(id);
    return ResponseUtils.success(result, "Lấy comment thành công");
  }

  @GetMapping
  @Operation(summary = "Danh sách comment", description = "Lấy danh sách comment với bộ lọc")
  public ResponseEntity<ApiResponseDto<java.util.List<CommentResponseDTO>>> getAll(
      @Parameter(description = "ID blog") @RequestParam(required = false) Integer blogId,
      @Parameter(description = "Trạng thái") @RequestParam(required = false) Boolean status,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Số lượng mỗi trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Trường sắp xếp") @RequestParam(defaultValue = "createdAt")
          String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {
    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<CommentResponseDTO> result = commentService.getAll(blogId, status, pageable);
    return ResponseUtils.successWithPage(result, "Lấy danh sách comment thành công");
  }

  @PutMapping("/{id}/status")
  @Operation(
      summary = "Cập nhật trạng thái comment",
      description = "Admin duyệt hoặc từ chối comment")
  public ResponseEntity<ApiResponseDto<CommentResponseDTO>> updateStatus(
      @Parameter(description = "ID comment") @PathVariable Integer id,
      @Parameter(description = "Trạng thái") @RequestParam Boolean status) {
    CommentResponseDTO result = commentService.updateStatus(id, status);
    return ResponseUtils.success(result, "Cập nhật trạng thái comment thành công");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa comment", description = "Admin xóa comment")
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID comment") @PathVariable Integer id) {
    commentService.delete(id);
    return ResponseUtils.success(null, "Xóa comment thành công");
  }
}


