package com.example.nikonbe.modules.promotion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Đối tượng phản hồi danh sách khuyến mãi có phân trang")
public class PromotionListResponseDTO {
  @Schema(description = "Danh sách khuyến mãi")
  private List<PromotionResponseDTO> content;

  @Schema(description = "Trang hiện tại", example = "0")
  private Integer currentPage;

  @Schema(description = "Tổng số khuyến mãi", example = "42")
  private Long totalItems;

  @Schema(description = "Tổng số trang", example = "5")
  private Integer totalPages;

  @Schema(description = "Kích thước trang", example = "10")
  private Integer pageSize;

  @Schema(description = "Có trang tiếp theo", example = "true")
  private Boolean hasNext;

  @Schema(description = "Có trang trước", example = "false")
  private Boolean hasPrevious;
}
