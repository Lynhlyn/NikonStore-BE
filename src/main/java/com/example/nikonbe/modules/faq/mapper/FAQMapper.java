package com.example.nikonbe.modules.faq.mapper;

import com.example.nikonbe.modules.content_category.entity.ContentCategory;
import com.example.nikonbe.modules.content_category.mapper.ContentCategoryMapper;
import com.example.nikonbe.modules.content_tag.entity.ContentTag;
import com.example.nikonbe.modules.content_tag.mapper.ContentTagMapper;
import com.example.nikonbe.modules.faq.dto.request.FAQCreateDTO;
import com.example.nikonbe.modules.faq.dto.request.FAQUpdateDTO;
import com.example.nikonbe.modules.faq.dto.response.FAQResponseDTO;
import com.example.nikonbe.modules.faq.entity.FAQ;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {ContentCategoryMapper.class, ContentTagMapper.class})
public interface FAQMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "category", expression = "java(fromCategoryId(dto.getCategoryId()))")
  @Mapping(target = "tag", expression = "java(fromTagId(dto.getTagId()))")
  FAQ toEntity(FAQCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "category", expression = "java(fromCategoryId(dto.getCategoryId()))")
  @Mapping(target = "tag", expression = "java(fromTagId(dto.getTagId()))")
  void updateEntityFromDto(FAQUpdateDTO dto, @MappingTarget FAQ entity);

  @Mapping(target = "category", source = "category")
  @Mapping(target = "tag", source = "tag")
  FAQResponseDTO toDto(FAQ entity);

  default ContentCategory fromCategoryId(Integer id) {
    if (id == null) return null;
    ContentCategory category = new ContentCategory();
    category.setId(id);
    return category;
  }

  default ContentTag fromTagId(Integer id) {
    if (id == null) return null;
    ContentTag tag = new ContentTag();
    tag.setId(id);
    return tag;
  }
}


