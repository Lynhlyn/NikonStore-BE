package com.example.nikonbe.modules.faq.service.impl;

import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.modules.content_category.repository.ContentCategoryRepository;
import com.example.nikonbe.modules.content_tag.repository.ContentTagRepository;
import com.example.nikonbe.modules.faq.dto.request.FAQCreateDTO;
import com.example.nikonbe.modules.faq.dto.request.FAQUpdateDTO;
import com.example.nikonbe.modules.faq.dto.response.FAQResponseDTO;
import com.example.nikonbe.modules.faq.entity.FAQ;
import com.example.nikonbe.modules.faq.mapper.FAQMapper;
import com.example.nikonbe.modules.faq.repository.FAQRepository;
import com.example.nikonbe.modules.faq.service.interF.FAQService;
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
public class FAQServiceImpl implements FAQService {

  private final FAQRepository faqRepository;
  private final FAQMapper faqMapper;
  private final ContentCategoryRepository contentCategoryRepository;
  private final ContentTagRepository contentTagRepository;

  @Override
  public FAQResponseDTO create(FAQCreateDTO dto) {
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

    FAQ faq = faqMapper.toEntity(dto);
    FAQ savedFAQ = faqRepository.save(faq);

    FAQ faqWithRelations =
        faqRepository
            .findByIdWithRelations(savedFAQ.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy FAQ"));

    return faqMapper.toDto(faqWithRelations);
  }

  @Override
  public FAQResponseDTO update(Integer id, FAQUpdateDTO dto) {
    FAQ faq =
        faqRepository
            .findByIdWithRelations(id)
            .orElseThrow(() -> new ResourceNotFoundException("FAQ không tồn tại"));

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

    faqMapper.updateEntityFromDto(dto, faq);

    if (dto.getStatus() != null) {
      faq.setStatus(dto.getStatus());
    }

    FAQ updatedFAQ = faqRepository.save(faq);
    return faqMapper.toDto(
        faqRepository
            .findByIdWithRelations(updatedFAQ.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy FAQ")));
  }

  @Override
  @Transactional(readOnly = true)
  public FAQResponseDTO getById(Integer id) {
    FAQ faq =
        faqRepository
            .findByIdWithRelations(id)
            .orElseThrow(() -> new ResourceNotFoundException("FAQ không tồn tại"));
    return faqMapper.toDto(faq);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<FAQResponseDTO> getAll(
      Integer categoryId, Integer tagId, Boolean status, Pageable pageable) {
    Page<FAQ> faqs = faqRepository.findAllWithFilters(categoryId, tagId, status, pageable);
    return faqs.map(faqMapper::toDto);
  }

  @Override
  public void delete(Integer id) {
    FAQ faq =
        faqRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("FAQ không tồn tại"));
    faqRepository.delete(faq);
  }

  @Override
  public FAQResponseDTO updateStatus(Integer id, Boolean status) {
    FAQ faq =
        faqRepository
            .findByIdWithRelations(id)
            .orElseThrow(() -> new ResourceNotFoundException("FAQ không tồn tại"));

    faq.setStatus(status);
    FAQ updatedFAQ = faqRepository.save(faq);
    return faqMapper.toDto(
        faqRepository
            .findByIdWithRelations(updatedFAQ.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy FAQ")));
  }
}


