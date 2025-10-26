package com.example.nikonbe.modules.content_category.service.interF;

import com.example.nikonbe.modules.content_category.dto.request.ContentCategoryCreateDTO;
import com.example.nikonbe.modules.content_category.dto.request.ContentCategoryUpdateDTO;
import com.example.nikonbe.modules.content_category.dto.response.ContentCategoryResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentCategoryService {
  ContentCategoryResponseDTO create(ContentCategoryCreateDTO dto);

  ContentCategoryResponseDTO update(Integer id, ContentCategoryUpdateDTO dto);

  ContentCategoryResponseDTO getById(Integer id);

  List<ContentCategoryResponseDTO> getAll(String name, String slug, String type);

  Page<ContentCategoryResponseDTO> getAllPaginated(
      String name, String slug, String type, Pageable pageable);

  void delete(Integer id);
}
