package com.example.nikonbe.common.helper.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.nikonbe.common.exceptions.BadRequestException;
import com.example.nikonbe.common.exceptions.CloudinaryException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class ImageUploadService {

  private final Cloudinary cloudinary;
  private final ExecutorService executorService;
  private static final List<String> ALLOWED_EXTENSIONS =
      Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "bmp", "tiff", "svg");
  private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
  private static final Pattern CLOUDINARY_URL_PATTERN =
      Pattern.compile(
          "https?://res\\.cloudinary\\.com/[^/]+/image/upload/(?:v\\d+/)?(.+)\\.[a-zA-Z]+");

  @Autowired
  public ImageUploadService(Cloudinary cloudinary) {
    this.cloudinary = cloudinary;
    this.executorService = Executors.newFixedThreadPool(10);
  }

  /**
   * Upload một file ảnh lên Cloudinary
   *
   * @param file File ảnh cần upload
   * @param folder Thư mục đích trên Cloudinary
   * @return URL của ảnh đã upload
   * @throws CloudinaryException Khi có lỗi xảy ra với Cloudinary
   * @throws BadRequestException Khi file không hợp lệ
   */
  public String uploadImage(MultipartFile file, String folder) {
    validateFile(file);
    validateFolder(folder);

    try {
      Map<String, Object> uploadParams =
          ObjectUtils.asMap(
              "folder", folder,
              "resource_type", "image",
              "quality", "auto",
              "fetch_format", "auto");

      Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
      String url = (String) uploadResult.get("secure_url");

      log.info("Successfully uploaded image to folder '{}': {}", folder, url);
      return url;

    } catch (IOException e) {
      log.error("Failed to upload image to folder '{}': {}", folder, e.getMessage());
      throw new CloudinaryException("Không thể upload ảnh: " + e.getMessage(), e);
    }
  }

  /**
   * Upload nhiều file ảnh lên Cloudinary song song
   *
   * @param files Danh sách file ảnh cần upload
   * @param folder Thư mục đích trên Cloudinary
   * @return Danh sách URL của các ảnh đã upload
   * @throws CloudinaryException Khi có lỗi xảy ra với Cloudinary
   * @throws BadRequestException Khi có file không hợp lệ
   */
  public List<String> uploadImages(List<MultipartFile> files, String folder) {
    if (files == null || files.isEmpty()) {
      throw new BadRequestException("Danh sách file không được để trống");
    }

    validateFolder(folder);

    // Validate tất cả files trước khi upload
    files.forEach(this::validateFile);

    List<CompletableFuture<String>> futures = new ArrayList<>();

    for (MultipartFile file : files) {
      CompletableFuture<String> future =
          CompletableFuture.supplyAsync(
              () -> {
                try {
                  return uploadImage(file, folder);
                } catch (Exception e) {
                  log.error(
                      "Failed to upload file '{}': {}", file.getOriginalFilename(), e.getMessage());
                  throw new CloudinaryException(
                      "Không thể upload file '"
                          + file.getOriginalFilename()
                          + "': "
                          + e.getMessage(),
                      e);
                }
              },
              executorService);

      futures.add(future);
    }

    try {
      List<String> urls = new ArrayList<>();
      for (CompletableFuture<String> future : futures) {
        urls.add(future.get());
      }

      log.info("Successfully uploaded {} images to folder '{}'", urls.size(), folder);
      return urls;

    } catch (Exception e) {
      log.error("Failed to upload multiple images: {}", e.getMessage());
      throw new CloudinaryException("Không thể upload nhiều ảnh: " + e.getMessage(), e);
    }
  }

  /**
   * Xóa một ảnh trên Cloudinary bằng URL
   *
   * @param imageUrl URL của ảnh cần xóa
   * @return true nếu xóa thành công, false nếu không tìm thấy ảnh
   * @throws CloudinaryException Khi có lỗi xảy ra với Cloudinary
   * @throws BadRequestException Khi URL không hợp lệ
   */
  public boolean deleteImage(String imageUrl) {
    if (!StringUtils.hasText(imageUrl)) {
      throw new BadRequestException("URL ảnh không được để trống");
    }

    String publicId = extractPublicIdFromUrl(imageUrl);
    if (publicId == null) {
      throw new BadRequestException("URL ảnh không hợp lệ: " + imageUrl);
    }

    try {
      Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
      String resultStatus = (String) result.get("result");

      boolean isDeleted = "ok".equals(resultStatus);

      if (isDeleted) {
        log.info("Successfully deleted image with public_id: {}", publicId);
      } else {
        log.warn("Image not found or already deleted with public_id: {}", publicId);
      }

      return isDeleted;

    } catch (IOException e) {
      log.error("Failed to delete image with public_id '{}': {}", publicId, e.getMessage());
      throw new CloudinaryException("Không thể xóa ảnh: " + e.getMessage(), e);
    }
  }

  /**
   * Xóa nhiều ảnh trên Cloudinary bằng danh sách URL
   *
   * @param imageUrls Danh sách URL của các ảnh cần xóa
   * @return Danh sách kết quả xóa (true/false) tương ứng với từng URL
   * @throws CloudinaryException Khi có lỗi xảy ra với Cloudinary
   * @throws BadRequestException Khi danh sách URL không hợp lệ
   */
  public List<Boolean> deleteImages(List<String> imageUrls) {
    if (imageUrls == null || imageUrls.isEmpty()) {
      throw new BadRequestException("Danh sách URL ảnh không được để trống");
    }

    List<CompletableFuture<Boolean>> futures = new ArrayList<>();

    for (String imageUrl : imageUrls) {
      CompletableFuture<Boolean> future =
          CompletableFuture.supplyAsync(
              () -> {
                try {
                  return deleteImage(imageUrl);
                } catch (Exception e) {
                  log.error("Failed to delete image '{}': {}", imageUrl, e.getMessage());
                  throw new CloudinaryException(
                      "Không thể xóa ảnh '" + imageUrl + "': " + e.getMessage(), e);
                }
              },
              executorService);

      futures.add(future);
    }

    try {
      List<Boolean> results = new ArrayList<>();
      for (CompletableFuture<Boolean> future : futures) {
        results.add(future.get());
      }

      long deletedCount = results.stream().mapToLong(result -> result ? 1 : 0).sum();
      log.info(
          "Successfully processed {} images, {} deleted, {} not found",
          results.size(),
          deletedCount,
          results.size() - deletedCount);

      return results;

    } catch (Exception e) {
      log.error("Failed to delete multiple images: {}", e.getMessage());
      throw new CloudinaryException("Không thể xóa nhiều ảnh: " + e.getMessage(), e);
    }
  }

  /**
   * Trích xuất public_id từ URL Cloudinary
   *
   * @param imageUrl URL của ảnh trên Cloudinary
   * @return public_id hoặc null nếu URL không hợp lệ
   */
  private String extractPublicIdFromUrl(String imageUrl) {
    if (!StringUtils.hasText(imageUrl)) {
      return null;
    }

    Matcher matcher = CLOUDINARY_URL_PATTERN.matcher(imageUrl);
    if (matcher.find()) {
      return matcher.group(1);
    }

    return null;
  }

  /**
   * Validate file upload
   *
   * @param file File cần validate
   * @throws BadRequestException Khi file không hợp lệ
   */
  private void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BadRequestException("File không được để trống");
    }

    if (file.getSize() > MAX_FILE_SIZE) {
      throw new BadRequestException(
          String.format(
              "Kích thước file '%s' vượt quá giới hạn %dMB",
              file.getOriginalFilename(), MAX_FILE_SIZE / (1024 * 1024)));
    }

    String originalFilename = file.getOriginalFilename();
    if (!StringUtils.hasText(originalFilename)) {
      throw new BadRequestException("Tên file không hợp lệ");
    }

    String fileExtension = getFileExtension(originalFilename).toLowerCase();
    if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
      throw new BadRequestException(
          String.format(
              "Định dạng file '%s' không được hỗ trợ. Các định dạng được hỗ trợ: %s",
              fileExtension, String.join(", ", ALLOWED_EXTENSIONS)));
    }
  }

  /**
   * Validate folder name
   *
   * @param folder Tên thư mục cần validate
   * @throws BadRequestException Khi tên thư mục không hợp lệ
   */
  private void validateFolder(String folder) {
    if (!StringUtils.hasText(folder)) {
      throw new BadRequestException("Tên thư mục không được để trống");
    }

    if (folder.contains("..") || folder.startsWith("/") || folder.endsWith("/")) {
      throw new BadRequestException("Tên thư mục không hợp lệ: " + folder);
    }
  }

  /**
   * Lấy phần mở rộng của file
   *
   * @param filename Tên file
   * @return Phần mở rộng của file
   */
  private String getFileExtension(String filename) {
    if (!StringUtils.hasText(filename)) {
      return "";
    }

    int lastDotIndex = filename.lastIndexOf('.');
    if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
      return "";
    }

    return filename.substring(lastDotIndex + 1);
  }
}
