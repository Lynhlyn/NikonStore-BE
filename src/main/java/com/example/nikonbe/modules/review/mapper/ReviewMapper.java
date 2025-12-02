package com.example.nikonbe.modules.review.mapper;

import com.example.nikonbe.modules.customer.mapper.CustomerMapper;
import com.example.nikonbe.modules.order_detail.entity.OrderDetail;
import com.example.nikonbe.modules.product.entity.Product;
import com.example.nikonbe.modules.review.dto.request.ReviewCreateDTO;
import com.example.nikonbe.modules.review.dto.request.ReviewUpdateDTO;
import com.example.nikonbe.modules.review.dto.response.ReviewImageResponseDTO;
import com.example.nikonbe.modules.review.dto.response.ReviewResponseDTO;
import com.example.nikonbe.modules.review.entity.Review;
import com.example.nikonbe.modules.review.entity.ReviewImage;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {CustomerMapper.class})
public interface ReviewMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "product", expression = "java(fromProductId(dto.getProductId()))")
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "orderDetail", expression = "java(fromOrderDetailId(dto.getOrderDetailId()))")
  @Mapping(target = "reviewImages", ignore = true)
  @Mapping(target = "status", constant = "1")
  Review toEntity(ReviewCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "orderDetail", ignore = true)
  @Mapping(target = "reviewImages", ignore = true)
  void updateEntityFromDto(ReviewUpdateDTO dto, @MappingTarget Review entity);

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "reviewImages", expression = "java(mapReviewImages(entity.getReviewImages()))")
  ReviewResponseDTO toDto(Review entity);

  default Product fromProductId(Integer id) {
    if (id == null) return null;
    Product product = new Product();
    product.setId(id);
    return product;
  }

  default OrderDetail fromOrderDetailId(Integer id) {
    if (id == null) return null;
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setId(id);
    return orderDetail;
  }

  default List<ReviewImageResponseDTO> mapReviewImages(List<ReviewImage> images) {
    if (images == null) return null;
    return images.stream()
        .map(
            img ->
                ReviewImageResponseDTO.builder()
                    .id(img.getId())
                    .imageUrl(img.getImageUrl())
                    .createdAt(img.getCreatedAt())
                    .updatedAt(img.getCreatedAt())
                    .build())
        .collect(Collectors.toList());
  }
}
