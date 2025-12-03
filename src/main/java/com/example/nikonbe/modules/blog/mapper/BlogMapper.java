package com.example.nikonbe.modules.blog.mapper;

import com.example.nikonbe.modules.content_category.entity.ContentCategory;
import com.example.nikonbe.modules.content_category.mapper.ContentCategoryMapper;
import com.example.nikonbe.modules.content_tag.entity.ContentTag;
import com.example.nikonbe.modules.content_tag.mapper.ContentTagMapper;
import com.example.nikonbe.modules.blog.dto.request.BlogCreateDTO;
import com.example.nikonbe.modules.blog.dto.request.BlogUpdateDTO;
import com.example.nikonbe.modules.blog.dto.response.BlogResponseDTO;
import com.example.nikonbe.modules.blog.entity.Blog;
import com.example.nikonbe.modules.staff.entity.Staff;
import com.example.nikonbe.modules.staff.mapper.StaffMapper;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {StaffMapper.class, ContentCategoryMapper.class, ContentTagMapper.class})
public interface BlogMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "staff", expression = "java(fromStaffId(dto.getStaffId()))")
  @Mapping(target = "category", expression = "java(fromCategoryId(dto.getCategoryId()))")
  @Mapping(target = "tag", expression = "java(fromTagId(dto.getTagId()))")
  @Mapping(target = "viewCount", constant = "0")
  Blog toEntity(BlogCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "staff", expression = "java(fromStaffId(dto.getStaffId()))")
  @Mapping(target = "category", expression = "java(fromCategoryId(dto.getCategoryId()))")
  @Mapping(target = "tag", expression = "java(fromTagId(dto.getTagId()))")
  @Mapping(target = "viewCount", ignore = true)
  void updateEntityFromDto(BlogUpdateDTO dto, @MappingTarget Blog entity);

  @Mapping(target = "staff", source = "staff")
  @Mapping(target = "category", source = "category")
  @Mapping(target = "tag", source = "tag")
  BlogResponseDTO toDto(Blog entity);

  default Staff fromStaffId(Integer id) {
    if (id == null) return null;
    Staff staff = new Staff();
    staff.setId(id);
    return staff;
  }

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


