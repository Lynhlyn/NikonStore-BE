package com.example.nikonbe.modules.content_tag.service.impl;

import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.content_tag.dto.request.ContentTagCreateDTO;
import com.example.nikonbe.modules.content_tag.dto.request.ContentTagUpdateDTO;
import com.example.nikonbe.modules.content_tag.dto.response.ContentTagResponseDTO;
import com.example.nikonbe.modules.content_tag.entity.ContentTag;
import com.example.nikonbe.modules.content_tag.mapper.ContentTagMapper;
import com.example.nikonbe.modules.content_tag.repository.ContentTagRepository;
import com.example.nikonbe.modules.content_tag.service.interF.ContentTagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentTagServiceImpl implements ContentTagService {
  private final ContentTagRepository repository;
  private final ContentTagMapper mapper;

  @Override
  @Transactional
  public ContentTagResponseDTO create(ContentTagCreateDTO dto) {
    validateCreateRequest(dto);
    ContentTag entity = mapper.toEntity(dto);
    ContentTag saved = repository.save(entity);
    return mapper.toDto(saved);
  }

  @Override
  @Transactional
  public ContentTagResponseDTO update(Integer id, ContentTagUpdateDTO dto) {
    ContentTag entity = findContentTagById(id);
    validateUpdateRequest(dto, entity);
    mapper.updateEntityFromDto(dto, entity);
    ContentTag updatedContentTag = repository.save(entity);
    return mapper.toDto(updatedContentTag);
  }

  @Override
  public ContentTagResponseDTO getById(Integer id) {
    ContentTag contentTag = findContentTagById(id);
    return mapper.toDto(contentTag);
  }

  @Override
  public List<ContentTagResponseDTO> getAll(String name, String slug, String type) {
    List<ContentTag> contentTags = repository.findAllByNameOrSlugOrType(name, slug, type);
    return mapper.toDtoList(contentTags);
  }

  @Override
  public Page<ContentTagResponseDTO> getAllPaginated(
      String name, String slug, String type, Pageable pageable) {
    Page<ContentTag> contentTags =
        repository.findAllByNameOrSlugOrTypePage(name, slug, type, pageable);
    return contentTags.map(mapper::toDto);
  }

  @Override
  @Transactional
  public void delete(Integer id) {
    ContentTag entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content_tag", "id", id));
    repository.delete(entity);
  }

  private void validateCreateRequest(ContentTagCreateDTO dto) {
    if (repository.existsByName(dto.getName())) {
      throw new ValidationException("Content_tag with this name already exists.");
    }
  }

  private ContentTag findContentTagById(Integer id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Content_tag", "id", id));
  }

  private void validateUpdateRequest(ContentTagUpdateDTO dto, ContentTag entity) {
    if (!entity.getName().equals(dto.getName()) && repository.existsByName(dto.getName())) {
      throw new ValidationException("Content_tag with this name already exists.");
    }
  }
}
