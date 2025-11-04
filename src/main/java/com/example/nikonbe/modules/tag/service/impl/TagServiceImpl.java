package com.example.nikonbe.modules.tag.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.tag.dto.request.TagCreateDTO;
import com.example.nikonbe.modules.tag.dto.request.TagUpdateDTO;
import com.example.nikonbe.modules.tag.dto.response.TagResponseDTO;
import com.example.nikonbe.modules.tag.entity.Tag;
import com.example.nikonbe.modules.tag.mapper.TagMapper;
import com.example.nikonbe.modules.tag.repository.TagRepository;
import com.example.nikonbe.modules.tag.service.interF.TagService;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {

  private final TagRepository tagRepository;
  private final TagMapper tagMapper;

  @Override
  @Transactional
  public TagResponseDTO create(TagCreateDTO dto) {
    validateCreateRequest(dto);

    Tag tag = tagMapper.toEntity(dto);
    Tag savedTag = tagRepository.save(tag);
    return tagMapper.toDto(savedTag);
  }

  @Override
  @Transactional
  public TagResponseDTO update(Integer id, TagUpdateDTO dto) {
    Tag tag = findTagById(id);
    validateUpdateRequest(dto, tag);

    tagMapper.updateEntityFromDto(dto, tag);
    Tag updatedTag = tagRepository.save(tag);

    return tagMapper.toDto(updatedTag);
  }

  @Override
  public TagResponseDTO getById(Integer id) {
    Tag tag = findTagById(id);
    return tagMapper.toDto(tag);
  }

  @Override
  public List<TagResponseDTO> getAll(String name, String slug, Status status) {
    List<Tag> tags = tagRepository.findAllByFilters(name, slug, status);
    return tagMapper.toDtoList(tags);
  }

  @Override
  public Page<TagResponseDTO> getAllPaginated(
      String name, String slug, Status status, Pageable pageable) {
    Page<Tag> tags = tagRepository.findAllPaginated(name, slug, status, pageable);
    return tags.map(tagMapper::toDto);
  }

  @Override
  @Transactional
  public void delete(Integer id) {
    Tag tag = findTagById(id);
    tag.setStatus(Status.DELETED);
    tagRepository.save(tag);
  }

  private Tag findTagById(Integer id) {
    return tagRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
  }

  private void validateCreateRequest(TagCreateDTO dto) {
    if (tagRepository.existsByName(dto.getName())) {
      throw new ValidationException("A tag with this name already exists.");
    }
    if (tagRepository.existsBySlug(dto.getSlug())) {
      throw new ValidationException("A tag with this slug already exists.");
    }
  }

  private void validateUpdateRequest(TagUpdateDTO dto, Tag tag) {
    if (!Objects.equals(tag.getName(), dto.getName())
        && tagRepository.existsByName(dto.getName())) {
      throw new ValidationException("A tag with this name already exists.");
    }
    if (!Objects.equals(tag.getSlug(), dto.getSlug())
        && tagRepository.existsBySlug(dto.getSlug())) {
      throw new ValidationException("A tag with this slug already exists.");
    }
  }
}
