package com.example.nikonbe.modules.content_category.service.impl;

import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.modules.content_category.dto.request.ContentCategoryCreateDTO;
import com.example.nikonbe.modules.content_category.dto.request.ContentCategoryUpdateDTO;
import com.example.nikonbe.modules.content_category.dto.response.ContentCategoryResponseDTO;
import com.example.nikonbe.modules.content_category.entity.ContentCategory;
import com.example.nikonbe.modules.content_category.mapper.ContentCategoryMapper;
import com.example.nikonbe.modules.content_category.repository.ContentCategoryRepository;
import com.example.nikonbe.modules.content_category.service.interF.ContentCategoryService;
import com.example.nikonbe.common.exceptions.ValidationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentCategoryServiceImpl implements ContentCategoryService {
  private final ContentCategoryRepository repository;
  private final ContentCategoryMapper mapper;

  @Override
  @Transactional
  public ContentCategoryResponseDTO create(ContentCategoryCreateDTO dto) {
    validateCreateRequest(dto);
    ContentCategory entity = mapper.toEntity(dto);
    ContentCategory saved = repository.save(entity);
    return mapper.toDto(saved);
  }

  @Override
  @Transactional
  public ContentCategoryResponseDTO update(Integer id, ContentCategoryUpdateDTO dto) {
    ContentCategory entity = findContentCategoryById(id);
    validateUpdateRequest(dto, entity);
    mapper.updateEntityFromDto(dto, entity);
    ContentCategory updatedContentCategory = repository.save(entity);
    return mapper.toDto(updatedContentCategory);
  }

  @Override
  @Transactional(readOnly = true)
  public ContentCategoryResponseDTO getById(Integer id) {
    ContentCategory entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content_category", "id", id));
    return mapper.toDto(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ContentCategoryResponseDTO> getAll(String name, String slug, String type) {
    List<ContentCategory> contentCategories =
        repository.findAllByNameOrSlugOrType(name, slug, type);
    return mapper.toDtoList(contentCategories);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ContentCategoryResponseDTO> getAllPaginated(
      String name, String slug, String type, Pageable pageable) {
    Page<ContentCategory> contentCategoryPage =
        repository.findAllByNameOrSlugOrTypePage(name, slug, type, pageable);
    return contentCategoryPage.map(mapper::toDto);
  }

  @Override
  @Transactional
  public void delete(Integer id) {
    ContentCategory entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content_category", "id", id));
    repository.delete(entity);
  }

  private void validateCreateRequest(ContentCategoryCreateDTO dto) {
    if (repository.existsByName(dto.getName())) {
      throw new ValidationException("Content_category with this name already exists.");
    }
  }

  private ContentCategory findContentCategoryById(Integer id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Content_category", "id", id));
  }

  private void validateUpdateRequest(ContentCategoryUpdateDTO dto, ContentCategory entity) {
    if (!entity.getName().equals(dto.getName()) && repository.existsByName(dto.getName())) {
      throw new ValidationException("Content_category with this name already exists.");
    }
    if (!entity.getSlug().equals(dto.getSlug()) && repository.existsBySlug(dto.getSlug())) {
      throw new ValidationException("Content_category with this slug already exists.");
    }
  }
}
