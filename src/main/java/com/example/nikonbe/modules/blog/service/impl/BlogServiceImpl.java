package com.example.nikonbe.modules.blog.service.impl;

import com.example.nikonbe.common.exceptions.BadRequestException;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.modules.blog.dto.request.BlogCreateDTO;
import com.example.nikonbe.modules.blog.dto.request.BlogUpdateDTO;
import com.example.nikonbe.modules.blog.dto.response.BlogResponseDTO;
import com.example.nikonbe.modules.blog.entity.Blog;
import com.example.nikonbe.modules.blog.mapper.BlogMapper;
import com.example.nikonbe.modules.blog.repository.BlogRepository;
import com.example.nikonbe.modules.blog.service.interF.BlogService;
import com.example.nikonbe.modules.content_category.repository.ContentCategoryRepository;
import com.example.nikonbe.modules.content_tag.repository.ContentTagRepository;
import com.example.nikonbe.modules.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BlogServiceImpl implements BlogService {

  private final BlogRepository blogRepository;
  private final BlogMapper blogMapper;
  private final StaffRepository staffRepository;
  private final ContentCategoryRepository contentCategoryRepository;
  private final ContentTagRepository contentTagRepository;

  @Override
  public BlogResponseDTO create(BlogCreateDTO dto) {
    if (blogRepository.findBySlug(dto.getSlug()).isPresent()) {
      throw new BadRequestException("Slug đã tồn tại");
    }

    if (dto.getStaffId() != null) {
      staffRepository
          .findById(dto.getStaffId())
          .orElseThrow(() -> new ResourceNotFoundException("Staff không tồn tại"));
    }

    if (dto.getCategoryId() != null) {
      contentCategoryRepository
          .findById(dto.getCategoryId())
          .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại"));
    }

    if (dto.getTagId() != null) {
      contentTagRepository
          .findById(dto.getTagId())
          .orElseThrow(() -> new ResourceNotFoundException("Tag không tồn tại"));
    }

    Blog blog = blogMapper.toEntity(dto);
    Blog savedBlog = blogRepository.save(blog);

    Blog blogWithRelations =
        blogRepository
            .findByIdWithRelations(savedBlog.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy blog"));

    return blogMapper.toDto(blogWithRelations);
  }

  @Override
  public BlogResponseDTO update(Integer id, BlogUpdateDTO dto) {
    Blog blog =
        blogRepository
            .findByIdWithRelations(id)
            .orElseThrow(() -> new ResourceNotFoundException("Blog không tồn tại"));

    if (dto.getSlug() != null && !dto.getSlug().equals(blog.getSlug())) {
      if (blogRepository.findBySlug(dto.getSlug()).isPresent()) {
        throw new BadRequestException("Slug đã tồn tại");
      }
    }

    if (dto.getStaffId() != null) {
      staffRepository
          .findById(dto.getStaffId())
          .orElseThrow(() -> new ResourceNotFoundException("Staff không tồn tại"));
    }

    if (dto.getCategoryId() != null) {
      contentCategoryRepository
          .findById(dto.getCategoryId())
          .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại"));
    }

    if (dto.getTagId() != null) {
      contentTagRepository
          .findById(dto.getTagId())
          .orElseThrow(() -> new ResourceNotFoundException("Tag không tồn tại"));
    }

    blogMapper.updateEntityFromDto(dto, blog);

    if (dto.getIsPublished() != null) {
      blog.setIsPublished(dto.getIsPublished());
    }

    Blog updatedBlog = blogRepository.save(blog);
    return blogMapper.toDto(
        blogRepository
            .findByIdWithRelations(updatedBlog.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy blog")));
  }

  @Override
  @Transactional(readOnly = true)
  public BlogResponseDTO getById(Integer id) {
    Blog blog =
        blogRepository
            .findByIdWithRelations(id)
            .orElseThrow(() -> new ResourceNotFoundException("Blog không tồn tại"));
    return blogMapper.toDto(blog);
  }

  @Override
  @Transactional(readOnly = true)
  public BlogResponseDTO getBySlug(String slug) {
    Blog blog =
        blogRepository
            .findBySlugWithRelations(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Blog không tồn tại"));
    return blogMapper.toDto(blog);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BlogResponseDTO> getAll(
      Integer categoryId,
      Integer tagId,
      Integer staffId,
      Boolean isPublished,
      String keyword,
      Pageable pageable) {
    Page<Blog> blogs =
        blogRepository.findAllWithFilters(
            categoryId, tagId, staffId, isPublished, keyword, pageable);
    return blogs.map(blogMapper::toDto);
  }

  @Override
  public void delete(Integer id) {
    Blog blog =
        blogRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Blog không tồn tại"));
    blogRepository.delete(blog);
  }

  @Override
  public BlogResponseDTO updatePublishStatus(Integer id, Boolean isPublished) {
    Blog blog =
        blogRepository
            .findByIdWithRelations(id)
            .orElseThrow(() -> new ResourceNotFoundException("Blog không tồn tại"));

    blog.setIsPublished(isPublished);
    Blog updatedBlog = blogRepository.save(blog);
    return blogMapper.toDto(
        blogRepository
            .findByIdWithRelations(updatedBlog.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy blog")));
  }

  @Override
  public void incrementViewCount(Integer id) {
    blogRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Blog không tồn tại"));
    blogRepository.incrementViewCount(id);
  }
}


