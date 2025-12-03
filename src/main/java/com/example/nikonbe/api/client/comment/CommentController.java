package com.example.nikonbe.api.client.comment;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.comment.dto.request.CommentCreateDTO;
import com.example.nikonbe.modules.comment.dto.response.CommentResponseDTO;
import com.example.nikonbe.modules.comment.service.interF.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.version}/comments")
@RequiredArgsConstructor
@Tag(name = "Client - Comment")
public class CommentController {

  private final CommentService commentService;

  @PostMapping
  @Operation(
      summary = "Tạo comment mới",
      description = "Khách hàng hoặc khách vãng lai tạo comment cho blog")
  public ResponseEntity<ApiResponseDto<CommentResponseDTO>> create(
      @Valid @RequestBody CommentCreateDTO dto) {
    CommentResponseDTO result = commentService.create(dto);
    return ResponseUtils.success(result, "Tạo comment thành công", HttpStatus.CREATED);
  }

  @GetMapping("/blog/{blogId}")
  @Operation(
      summary = "Lấy danh sách comment theo blog",
      description = "Lấy danh sách comment đã được duyệt của một blog")
  public ResponseEntity<ApiResponseDto<java.util.List<CommentResponseDTO>>> getByBlogId(
      @Parameter(description = "ID blog") @PathVariable Integer blogId,
      @Parameter(description = "Trạng thái (true: đã duyệt, false: chưa duyệt)")
          @RequestParam(required = false, defaultValue = "true")
          Boolean status) {
    List<CommentResponseDTO> result = commentService.getByBlogId(blogId, status);
    return ResponseUtils.success(result, "Lấy danh sách comment thành công");
  }
}


