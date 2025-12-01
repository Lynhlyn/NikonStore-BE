package com.example.nikonbe.modules.page.service.impl;

import com.example.nikonbe.common.exceptions.BadRequestException;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.modules.page.dto.request.PageCreateDto;
import com.example.nikonbe.modules.page.dto.request.PageUpdateDto;
import com.example.nikonbe.modules.page.dto.response.PageAdminDto;
import com.example.nikonbe.modules.page.dto.response.PageDto;
import com.example.nikonbe.modules.page.entity.Page;
import com.example.nikonbe.modules.page.mapper.PageMapper;
import com.example.nikonbe.modules.page.repository.PageRepository;
import com.example.nikonbe.modules.page.service.interF.PageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PageServiceImpl implements PageService {

  private final PageRepository pageRepository;
  private final PageMapper pageMapper;

  @Override
  @Transactional
  public PageAdminDto create(PageCreateDto dto) {
    String slug = normalizeSlug(dto.getSlug());

    if (pageRepository.existsBySlug(slug)) {
      throw new BadRequestException("Slug '" + slug + "' đã tồn tại trong hệ thống");
    }

    Page page = pageMapper.toEntity(dto);
    page.setSlug(slug);

    Page savedPage = pageRepository.save(page);
    log.info("Created page with id: {} and slug: {}", savedPage.getId(), savedPage.getSlug());

    return pageMapper.toAdminDto(savedPage);
  }

  @Override
  @Transactional
  public PageAdminDto update(Long id, PageUpdateDto dto) {
    Page page =
        pageRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy page với ID: " + id));

    if (dto.getSlug() != null) {
      String slug = normalizeSlug(dto.getSlug());
      if (!slug.equals(page.getSlug()) && pageRepository.existsBySlugAndIdNot(slug, id)) {
        throw new BadRequestException("Slug '" + slug + "' đã tồn tại trong hệ thống");
      }
      page.setSlug(slug);
    }

    pageMapper.updateEntityFromDto(dto, page);

    Page updatedPage = pageRepository.save(page);
    log.info("Updated page with id: {}", updatedPage.getId());

    return pageMapper.toAdminDto(updatedPage);
  }

  @Override
  @Transactional(readOnly = true)
  public PageAdminDto getByPageKey(String pageKey) {
    return pageRepository
        .findBySlug(pageKey)
        .map(pageMapper::toAdminDto)
        .orElse(new PageAdminDto());
  }

  @Override
  @Transactional(readOnly = true)
  public PageDto getBySlugForClient(String slug) {
    return pageRepository.findBySlug(slug).map(pageMapper::toClientDto).orElse(new PageDto());
  }

  private String normalizeSlug(String slug) {
    String normalized =
        slug
            .toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .trim()
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-");
    if (normalized.isBlank()) {
      throw new BadRequestException("Slug không hợp lệ");
    }
    return normalized;
  }
}
