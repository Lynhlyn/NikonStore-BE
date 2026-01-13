package com.example.nikonbe.modules.pos.dto.response;

import com.example.nikonbe.modules.attributes.capacity.dto.response.CapacityResponseDTO;
import com.example.nikonbe.modules.attributes.color.dto.response.ColorResponseDTO;
import com.example.nikonbe.modules.promotion.dto.response.PromotionResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin chi tiết đơn hàng POS")
public class PosOrderDetailResponse {

  @Schema(description = "ID chi tiết đơn hàng", example = "1")
  private Integer id;

  @Schema(description = "ID chi tiết sản phẩm", example = "1")
  private Integer productDetailId;

  @Schema(description = "SKU sản phẩm", example = "IP14-RED-256")
  private String sku;

  @Schema(description = "Tên sản phẩm", example = "iPhone 14")
  private String productName;

  @Schema(description = "Màu sắc")
  private ColorResponseDTO color;

  @Schema(description = "dung tích")
  private CapacityResponseDTO capacity;

  @Schema(description = "Số lượng", example = "2")
  private Integer quantity;

  @Schema(description = "Giá gốc từ product detail (chưa áp dụng promotion)", example = "3000000")
  private BigDecimal originalPrice;

  @Schema(description = "Giá sau khi áp dụng promotion (hoặc bằng giá gốc nếu không có promotion)", example = "1800000")
  private BigDecimal price;

  @Schema(
      description = "Giảm giá - là giá trị của product detail sau khi  giam  gia promotion",
      example = "2500000")
  private BigDecimal discount;

  @Schema(description = "Thành tiền", example = "44980000")
  private BigDecimal totalAmount;

  @Schema(description = "Khuyến mãi áp dụng")
  private PromotionResponseDTO promotion;

  @Schema(description = "Ảnh thumbnail", example = "https://example.com/image.jpg")
  private String thumbnailImage;
}
