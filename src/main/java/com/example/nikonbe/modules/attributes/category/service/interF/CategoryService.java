package com.example.nikonbe.modules.attributes.category.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.attributes.category.dto.request.CategoryCreateDTO;
import com.example.nikonbe.modules.attributes.category.dto.request.CategoryUpdateDTO;
import com.example.nikonbe.modules.attributes.category.dto.response.CategoryResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
  CategoryResponseDTO create(CategoryCreateDTO dto);

  CategoryResponseDTO update(Integer id, CategoryUpdateDTO dto);

  CategoryResponseDTO getById(Integer id);

  List<CategoryResponseDTO> getAll();

  Page<CategoryResponseDTO> getAllPaginated(Pageable pageable);

  List<CategoryResponseDTO> getAllByStatus(Status status);

  Page<CategoryResponseDTO> getAllByStatusPaginated(Status status, Pageable pageable);

  void delete(Integer id);

  boolean existsByName(String name);

  Page<CategoryResponseDTO> search(String keyword, Status status, Pageable pageable);
}
