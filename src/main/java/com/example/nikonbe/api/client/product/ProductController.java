package com.example.nikonbe.api.client.product;

import com.example.nikonbe.common.response.ApiResponseDto;
import com.example.nikonbe.common.utils.PaginationUtils;
import com.example.nikonbe.common.utils.ResponseUtils;
import com.example.nikonbe.modules.product.dto.response.ProductDetailFullResponseDTO;
import com.example.nikonbe.modules.product.dto.response.ProductListingResponseDTO;
import com.example.nikonbe.modules.product.service.interF.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/products")
@Tag(name = "Client - Product")
public class ProductController {

  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Lấy chi tiết sản phẩm",
      description = "Lấy thông tin chi tiết sản phẩm bao gồm tất cả biến thể, tags và features")
  @ApiResponse(responseCode = "200", description = "Lấy chi tiết sản phẩm thành công")
  public ResponseEntity<ApiResponseDto<ProductDetailFullResponseDTO>> getProductDetail(
      @Parameter(description = "ID sản phẩm") @PathVariable Integer id) {
    ProductDetailFullResponseDTO result = service.getProductDetail(id);
    return ResponseUtils.success(result, "Lấy chi tiết sản phẩm thành công");
  }

  @GetMapping
  @Operation(
      summary = "Lấy danh sách sản phẩm với bộ lọc nâng cao",
      description = "API chính để hiển thị danh sách sản phẩm cho khách hàng với nhiều bộ lọc")
  @ApiResponse(responseCode = "200", description = "Lấy danh sách sản phẩm thành công")
  public ResponseEntity<ApiResponseDto<List<ProductListingResponseDTO>>> getProductListings(
      @Parameter(description = "Từ khóa tìm kiếm theo tên sản phẩm") @RequestParam(required = false)
          String keyword,
      @Parameter(description = "ID thương hiệu (có thể nhiều)") @RequestParam(required = false)
          List<Integer> brandIds,
      @Parameter(description = "ID loại dây đeo (có thể nhiều)") @RequestParam(required = false)
          List<Integer> strapTypeIds,
      @Parameter(description = "ID chất liệu (có thể nhiều)") @RequestParam(required = false)
          List<Integer> materialIds,
      @Parameter(description = "ID danh mục (có thể nhiều)") @RequestParam(required = false)
          List<Integer> categoryIds,
      @Parameter(description = "ID màu sắc (có thể nhiều)") @RequestParam(required = false)
          List<Integer> colorIds,
      @Parameter(description = "ID dung tích (có thể nhiều)") @RequestParam(required = false)
          List<Integer> capacityIds,
      @Parameter(description = "ID tag (có thể nhiều)") @RequestParam(required = false)
          List<Integer> tagIds,
      @Parameter(description = "ID tính năng (có thể nhiều)") @RequestParam(required = false)
          List<Integer> featureIds,
      @Parameter(description = "Giá tối thiểu") @RequestParam(required = false) BigDecimal minPrice,
      @Parameter(description = "Giá tối đa") @RequestParam(required = false) BigDecimal maxPrice,
      @Parameter(description = "Lọc sản phẩm có khuyến mãi") @RequestParam(required = false)
          Boolean hasPromotion,
      @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0")
          int page,
      @Parameter(description = "Số sản phẩm trên mỗi trang") @RequestParam(defaultValue = "12")
          int size,
      @Parameter(description = "Trường sắp xếp") @RequestParam(defaultValue = "id") String sort,
      @Parameter(description = "Hướng sắp xếp (asc/desc)") @RequestParam(defaultValue = "asc")
          String direction) {

    Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);

    Page<ProductListingResponseDTO> result =
        service.getProductListings(
            keyword,
            brandIds,
            strapTypeIds,
            materialIds,
            categoryIds,
            colorIds,
            capacityIds,
            tagIds,
            featureIds,
            minPrice,
            maxPrice,
            hasPromotion,
            pageable);

    return ResponseUtils.successWithPage(result, "Lấy danh sách sản phẩm thành công");
  }
}
