package com.example.nikonbe.modules.attributes.category.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.attributes.category.dto.request.CategoryCreateDTO;
import com.example.nikonbe.modules.attributes.category.dto.request.CategoryUpdateDTO;
import com.example.nikonbe.modules.attributes.category.dto.response.CategoryResponseDTO;
import com.example.nikonbe.modules.attributes.category.entity.Category;
import com.example.nikonbe.modules.attributes.category.mapper.CategoryMapper;
import com.example.nikonbe.modules.attributes.category.repository.CategoryRepository;
import com.example.nikonbe.modules.attributes.category.service.interF.CategoryService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  @Transactional
  @Override
  public CategoryResponseDTO create(CategoryCreateDTO dto) {
    Map<String, String> errors = new HashMap<>();

    if (categoryRepository.existsByName(dto.getName())) {
      errors.put("name", "Category name already exists");
    }

    Category category = categoryMapper.toEntity(dto);

    if (dto.getParentId() != null) {
      if (dto.getParentId().equals(category.getId())) {
        errors.put("parentId", "Parent category cannot be itself");
      } else {
        Category parent = categoryRepository.findById(dto.getParentId()).orElse(null);
        if (parent == null) {
          errors.put("parentId", "Parent category does not exist");
        } else {
          category.setParent(parent);
        }
      }
    } else {
      category.setParent(null);
    }

    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }

    Category saved = categoryRepository.save(category);
    return categoryMapper.toDto(saved);
  }

  @Transactional
  @Override
  public CategoryResponseDTO update(Integer id, CategoryUpdateDTO dto) {
    Map<String, String> errors = new HashMap<>();

    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

    if (categoryRepository.existsByNameAndIdNot(dto.getName(), id)) {
      errors.put("name", "Category name already exists");
    }

    if (dto.getParentId() != null) {
      if (dto.getParentId().equals(id)) {
        errors.put("parentId", "Parent category cannot be itself");
      } else {
        Category parent = categoryRepository.findById(dto.getParentId()).orElse(null);
        if (parent == null) {
          errors.put("parentId", "Parent category does not exist");
        } else {
          category.setParent(parent);
        }
      }
    } else {
      category.setParent(null);
    }

    if (!errors.isEmpty()) {
      throw new ValidationException("Validation failed", errors);
    }

    Status oldStatus = category.getStatus();
    categoryMapper.updateEntityFromDto(dto, category);
    Category updated = categoryRepository.save(category);

    return categoryMapper.toDto(updated);
  }

  @Transactional(readOnly = true)
  @Override
  public CategoryResponseDTO getById(Integer id) {
    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    return categoryMapper.toDto(category);
  }

  @Transactional(readOnly = true)
  @Override
  public List<CategoryResponseDTO> getAll() {
    List<Category> categories = categoryRepository.findAll();
    return categoryMapper.toDtoList(categories);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<CategoryResponseDTO> getAllPaginated(Pageable pageable) {
    Page<Category> page = categoryRepository.findAll(pageable);
    return page.map(categoryMapper::toDto);
  }

  @Transactional(readOnly = true)
  @Override
  public List<CategoryResponseDTO> getAllByStatus(Status status) {
    List<Category> categories = categoryRepository.findByStatus(status);
    return categoryMapper.toDtoList(categories);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<CategoryResponseDTO> getAllByStatusPaginated(Status status, Pageable pageable) {
    Page<Category> page = categoryRepository.findByStatus(status, pageable);
    return page.map(categoryMapper::toDto);
  }

  @Transactional
  @Override
  public void delete(Integer id) {
    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    category.setStatus(Status.DELETED);
    categoryRepository.save(category);
  }

  @Override
  public boolean existsByName(String name) {
    return categoryRepository.existsByName(name);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<CategoryResponseDTO> search(String keyword, Status status, Pageable pageable) {
    if (status != null) {
      return categoryRepository
          .findByNameContainingIgnoreCaseAndStatus(keyword, status, pageable)
          .map(categoryMapper::toDto);
    }
    return categoryRepository
        .findByNameContainingIgnoreCase(keyword, pageable)
        .map(categoryMapper::toDto);
  }
}
