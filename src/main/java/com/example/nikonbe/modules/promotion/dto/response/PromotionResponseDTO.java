package com.example.nikonbe.modules.promotion.dto.response;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.product.dto.response.ProductResponseDTO;
import com.example.nikonbe.modules.product_detail.dto.response.ProductDetailResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng phản hồi thông tin khuyến mãi")
public class PromotionResponseDTO {
  @Schema(description = "ID khuyến mãi", example = "1")
  private Integer id;

  @Schema(description = "Tên khuyến mãi", example = "Flash Sale Tết")
  private String name;

  @Schema(description = "Tiêu đề khuyến mãi", example = "Giảm giá 30%")
  private String title;

  @Schema(description = "Mã khuyến mãi", example = "TET2024")
  private String code;

  @Schema(description = "Loại giảm giá", example = "percentage")
  private String discountType;

  @Schema(description = "Giá trị giảm giá", example = "30.0")
  private BigDecimal discountValue;

  @Schema(description = "Đối tượng áp dụng", example = "product")
  private String appliesTo;

  @Schema(description = "ID sản phẩm/danh mục áp dụng", example = "1")
  private String appliedProduct;

  @Schema(description = "Ngày bắt đầu", example = "2024-01-20T00:00:00")
  private LocalDateTime startDate;

  @Schema(description = "Ngày kết thúc", example = "2024-01-25T23:59:59")
  private LocalDateTime endDate;

  @Schema(description = "Mô tả khuyến mãi", example = "Chương trình khuyến mãi dịp Tết")
  private String description;

  @Schema(description = "Trạng thái", example = "ACTIVE")
  private Status status;

  @Schema(description = "Thời gian tạo", example = "2023-12-20T10:15:30")
  private LocalDateTime createdAt;

  @Schema(description = "Thời gian cập nhật", example = "2023-12-21T14:20:45")
  private LocalDateTime updatedAt;

  @Schema(description = "Danh sách sản phẩm đang áp dụng khuyến mãi này")
  private List<ProductResponseDTO> products;

  @Schema(description = "Danh sách sản phẩm chi tiết đang áp dụng khuyến mãi này")
  private List<ProductDetailResponseDTO> productDetails;

  @Schema(description = "Văn bản loại giảm giá", example = "Phần trăm")
  public String getDiscountTypeText() {
    return "percentage".equals(discountType) ? "Phần trăm" : "Số tiền cố định";
  }

  @Schema(description = "Văn bản đối tượng áp dụng", example = "Sản phẩm cụ thể")
  public String getAppliesToText() {
    if ("all".equals(appliesTo)) {
      return "Tất cả sản phẩm";
    } else if ("category".equals(appliesTo)) {
      return "Danh mục sản phẩm";
    } else if ("product".equals(appliesTo)) {
      return "Sản phẩm cụ thể";
    }
    return appliesTo;
  }

  @Schema(description = "Còn hiệu lực", example = "true")
  public Boolean getIsActive() {
    return status == Status.ACTIVE;
  }

  @Schema(description = "Đã hết hạn", example = "false")
  public Boolean getIsExpired() {
    return endDate != null && LocalDateTime.now().isAfter(endDate);
  }
}
