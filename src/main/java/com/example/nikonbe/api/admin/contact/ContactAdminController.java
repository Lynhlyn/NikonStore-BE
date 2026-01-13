package com.example.nikonbe.api.admin.contact;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.contact.dto.request.ContactCreateDTO;
import com.example.nikonbe.modules.contact.dto.request.ContactUpdateDTO;
import com.example.nikonbe.modules.contact.dto.response.ContactResponseDTO;
import com.example.nikonbe.modules.contact.service.interF.ContactService;
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
@RequestMapping("${api.admin.version}/contacts")
@RequiredArgsConstructor
@Tag(name = "Admin - Contact Management", description = "Các API quản lý liên hệ cho admin")
public class ContactAdminController {

  private final ContactService contactService;

  @PostMapping
  @Operation(summary = "Tạo mới liên hệ")
  @ApiResponse(
      responseCode = "201",
      description = "Tạo thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<ContactResponseDTO>> create(
      @Valid @RequestBody ContactCreateDTO dto) {
    ContactResponseDTO result = contactService.create(dto);
    return ResponseUtils.success(result, "Contact created successfully", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Cập nhật liên hệ")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Cập nhật thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy liên hệ")
  })
  public ResponseEntity<ApiResponseDto<ContactResponseDTO>> update(
      @Parameter(description = "ID liên hệ") @PathVariable Integer id,
      @Valid @RequestBody ContactUpdateDTO dto) {
    ContactResponseDTO result = contactService.update(id, dto);
    return ResponseUtils.success(result, "Contact updated successfully");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy liên hệ theo ID và đánh dấu đã xem")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lấy thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy liên hệ")
  })
  public ResponseEntity<ApiResponseDto<ContactResponseDTO>> getById(
      @Parameter(description = "ID liên hệ") @PathVariable Integer id) {
    ContactResponseDTO result = contactService.getByIdAndMarkAsRead(id);
    return ResponseUtils.success(result, "Contact retrieved successfully");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách liên hệ",
      description = "Có thể lấy tất cả hoặc phân trang. Có thể lọc theo trạng thái.")
  @ApiResponse(
      responseCode = "200",
      description = "Lấy thành công",
      content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
  public ResponseEntity<ApiResponseDto<List<ContactResponseDTO>>> getAll(
      @Parameter(description = "Lấy tất cả không phân trang") @RequestParam(defaultValue = "false")
          boolean isAll,
      @Parameter(description = "Lọc theo trạng thái") @RequestParam(required = false) Status status,
      @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Sắp xếp theo") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc")
          String direction) {

    if (isAll) {
      List<ContactResponseDTO> result =
          status != null ? contactService.getAllByStatus(status) : contactService.getAll();
      return ResponseUtils.success(result, "Contacts retrieved successfully");
    }

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
    Page<ContactResponseDTO> result =
        status != null
            ? contactService.getAllByStatusPaginated(status, pageable)
            : contactService.getAllPaginated(pageable);

    return ResponseUtils.successWithPage(result, "Contacts retrieved successfully");
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa liên hệ", description = "Đánh dấu liên hệ là DELETED")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Xóa thành công",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy liên hệ")
  })
  public ResponseEntity<ApiResponseDto<Void>> delete(
      @Parameter(description = "ID liên hệ") @PathVariable Integer id) {
    contactService.delete(id);
    return ResponseUtils.success(null, "Contact deleted successfully");
  }
}
