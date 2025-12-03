package com.example.nikonbe.modules.blog.service.interF;

import com.example.nikonbe.modules.blog.dto.request.BlogCreateDTO;
import com.example.nikonbe.modules.blog.dto.request.BlogUpdateDTO;
import com.example.nikonbe.modules.blog.dto.response.BlogResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogService {

  BlogResponseDTO create(BlogCreateDTO dto);

  BlogResponseDTO update(Integer id, BlogUpdateDTO dto);

  BlogResponseDTO getById(Integer id);

  BlogResponseDTO getBySlug(String slug);

  Page<BlogResponseDTO> getAll(
      Integer categoryId,
      Integer tagId,
      Integer staffId,
      Boolean isPublished,
      String keyword,
      Pageable pageable);

  void delete(Integer id);

  BlogResponseDTO updatePublishStatus(Integer id, Boolean isPublished);

  void incrementViewCount(Integer id);
}


