package com.example.nikonbe.api.admin.upload;

import com.example.nikonbe.common.helper.cloudinary.service.ImageUploadService;
import com.example.nikonbe.common.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("${api.admin.version}/upload")
@Tag(name = "Admin - File Upload", description = "API quản lý tải lên và xóa hình ảnh cho admin")
@Validated
public class ImageUploadAdminController {

  private final ImageUploadService imageUploadService;

  @Autowired
  public ImageUploadAdminController(ImageUploadService imageUploadService) {
    this.imageUploadService = imageUploadService;
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      summary = "Tải lên hình ảnh (Admin)",
      description =
          "Tải lên một hoặc nhiều file hình ảnh vào thư mục được chỉ định. Hỗ trợ upload song song để tối ưu hiệu suất. Yêu cầu quyền admin hoặc staff.")
  @ApiResponse(
      responseCode = "200",
      description = "Tải lên thành công",
      content =
          @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
  @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ")
  @ApiResponse(responseCode = "401", description = "Không có quyền truy cập")
  @ApiResponse(responseCode = "500", description = "Lỗi server khi tải lên")
  public ResponseEntity<?> uploadImages(
      @Parameter(description = "Danh sách file hình ảnh cần tải lên", required = true)
          @RequestParam("files")
          List<MultipartFile> files,
      @Parameter(
              description = "Tên thư mục lưu trữ trên Cloudinary",
              required = true,
              example = "banners")
          @RequestParam("folder")
          @NotBlank(message = "Tên thư mục không được để trống")
          String folder) {
    log.info("Received admin upload request for {} files to folder '{}'", files.size(), folder);

    List<String> urls;
    if (files.size() == 1) {
      String url = imageUploadService.uploadImage(files.get(0), folder);
      urls = List.of(url);
    } else {
      urls = imageUploadService.uploadImages(files, folder);
    }

    log.info("Successfully uploaded {} images to folder '{}'", urls.size(), folder);
    return ResponseUtils.success(
        urls, String.format("Đã tải lên thành công %d ảnh", urls.size()), HttpStatus.OK);
  }

  @DeleteMapping
  @Operation(
      summary = "Xóa hình ảnh (Admin)",
      description =
          "Xóa một hoặc nhiều hình ảnh trên Cloudinary bằng URL. Hỗ trợ xóa song song để tối ưu hiệu suất. Yêu cầu quyền admin hoặc staff.")
  @ApiResponse(
      responseCode = "200",
      description = "Xóa thành công",
      content =
          @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
  @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ")
  @ApiResponse(responseCode = "401", description = "Không có quyền truy cập")
  @ApiResponse(responseCode = "500", description = "Lỗi server khi xóa")
  public ResponseEntity<?> deleteImages(
      @Parameter(description = "Danh sách URL của các ảnh cần xóa", required = true)
          @RequestBody
          @NotEmpty(message = "Danh sách URL không được để trống")
          List<@NotBlank(message = "URL không được để trống") String> imageUrls) {

    log.info("Received admin delete request for {} images", imageUrls.size());

    List<Boolean> results;
    if (imageUrls.size() == 1) {
      boolean result = imageUploadService.deleteImage(imageUrls.get(0));
      results = List.of(result);
    } else {
      results = imageUploadService.deleteImages(imageUrls);
    }

    long deletedCount = results.stream().mapToLong(r -> r ? 1 : 0).sum();
    long notFoundCount = results.size() - deletedCount;

    String message = String.format("Đã xóa thành công %d ảnh", deletedCount);
    if (notFoundCount > 0) {
      message += String.format(", %d ảnh không tìm thấy", notFoundCount);
    }

    log.info("Delete operation completed: {} deleted, {} not found", deletedCount, notFoundCount);
    return ResponseUtils.success(results, message, HttpStatus.OK);
  }

  @DeleteMapping("/{imageUrl:.+}")
  @Operation(
      summary = "Xóa một hình ảnh (Admin)",
      description = "Xóa một hình ảnh trên Cloudinary bằng URL được truyền qua path parameter. Yêu cầu quyền admin hoặc staff.")
  @ApiResponse(
      responseCode = "200",
      description = "Xóa thành công",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = Boolean.class)))
  @ApiResponse(responseCode = "400", description = "URL không hợp lệ")
  @ApiResponse(responseCode = "401", description = "Không có quyền truy cập")
  @ApiResponse(responseCode = "500", description = "Lỗi server khi xóa")
  public ResponseEntity<?> deleteImage(
      @Parameter(
              description = "URL của ảnh cần xóa (được encode)",
              required = true,
              example = "https://res.cloudinary.com/example/image/upload/v123/banners/x.png")
          @PathVariable
          @NotBlank(message = "URL không được để trống")
          String imageUrl) {

    log.info("Received admin delete request for single image: {}", imageUrl);

    boolean result = imageUploadService.deleteImage(imageUrl);

    String message =
        result ? "Đã xóa ảnh thành công" : "Ảnh không tìm thấy hoặc đã được xóa trước đó";

    log.info("Delete single image result: {}", result);
    return ResponseUtils.success(result, message, HttpStatus.OK);
  }
}

