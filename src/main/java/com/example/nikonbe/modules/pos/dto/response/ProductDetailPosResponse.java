package com.example.nikonbe.modules.pos.dto.response;

import com.example.nikonbe.common.enums.Status;
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
@Schema(description = "Thông tin phản hồi của chi tiết sản phẩm với ảnh")
public class ProductDetailPosResponse {
  @Schema(description = "ID chi tiết sản phẩm", example = "1")
  private Integer id;

  @Schema(description = "SKU", example = "IP14-RED-256")
  private String sku;

  @Schema(description = "Số lượng tồn kho", example = "100")
  private Integer stock;

  @Schema(description = "Số lượng đặt trước", example = "5")
  private Integer reservedStock;

  @Schema(description = "Số lượng có thể bán", example = "95")
  private Integer availableStock;

  @Schema(description = "Tên sản phẩm", example = "iPhone 14")
  private String productName;

  @Schema(description = "Màu sắc", example = "Đỏ")
  private ColorResponseDTO color;

  @Schema(description = "Dung tích", example = "21L")
  private CapacityResponseDTO capacity;

  @Schema(description = "Giá bán", example = "24990000")
  private BigDecimal price;

  @Schema(description = "Trạng thái", example = "ACTIVE")
  private Status status;

  @Schema(description = "Khuyến mãi", example = "3")
  private PromotionResponseDTO promotion;

  @Schema(description = "Ảnh thumbnail của biến thể", example = "https://example.com/image.jpg")
  private String thumbnailImage;
}
