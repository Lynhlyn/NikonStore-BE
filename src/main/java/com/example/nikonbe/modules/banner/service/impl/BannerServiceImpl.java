package com.example.nikonbe.modules.banner.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.exceptions.ResourceAlreadyExistsException;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.modules.banner.dto.request.BannerCreateDTO;
import com.example.nikonbe.modules.banner.dto.request.BannerUpdateDTO;
import com.example.nikonbe.modules.banner.dto.response.BannerResponseDTO;
import com.example.nikonbe.modules.banner.entity.Banner;
import com.example.nikonbe.modules.banner.mapper.BannerMapper;
import com.example.nikonbe.modules.banner.repository.BannerRepository;
import com.example.nikonbe.modules.banner.service.interF.BannerService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
public class BannerServiceImpl implements BannerService {

  private final BannerRepository repository;
  private final BannerMapper mapper;

  @Override
  public BannerResponseDTO create(BannerCreateDTO dto) {
    log.info("Tạo mới banner với tên: {}", dto.getName());

    if (repository.existsByNameAndIdNot(dto.getName(), 0L)) {
      throw new ResourceAlreadyExistsException("Banner với tên '" + dto.getName() + "' đã tồn tại");
    }

    Banner entity = mapper.toEntity(dto);
    entity.setCreatedAt(LocalDateTime.now());
    entity.setUpdatedAt(LocalDateTime.now());

    Banner saved = repository.save(entity);
    log.info("Tạo banner thành công với ID: {}", saved.getId());

    return mapper.toDto(saved);
  }

  @Override
  public BannerResponseDTO update(Long id, BannerUpdateDTO dto) {
    log.info("Cập nhật banner với ID: {}", id);

    Banner entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Banner", "id", id));

    if (repository.existsByNameAndIdNot(dto.getName(), id)) {
      throw new ResourceAlreadyExistsException("Banner với tên '" + dto.getName() + "' đã tồn tại");
    }

    mapper.updateEntityFromDto(dto, entity);
    entity.setUpdatedAt(LocalDateTime.now());

    Banner updated = repository.save(entity);
    log.info("Cập nhật banner thành công với ID: {}", id);

    return mapper.toDto(updated);
  }

  @Override
  @Transactional(readOnly = true)
  public BannerResponseDTO getById(Long id) {
    log.info("Lấy banner với ID: {}", id);

    Banner entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Banner", "id", id));

    return mapper.toDto(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BannerResponseDTO> getAll(Status status, Integer position) {
    log.info("Lấy danh sách banner với status: {}, position: {}", status, position);

    List<Banner> banners;
    if (position != null) {
      banners = repository.findAllWithFiltersList(status, position);
    } else {
      banners = repository.findByStatusOrderByDisplayOrderAsc(status);
    }

    return banners.stream().map(mapper::toDto).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BannerResponseDTO> getAllPaginated(
      Status status, Integer position, Pageable pageable) {
    log.info("Lấy danh sách banner phân trang với status: {}, position: {}", status, position);

    Page<Banner> bannersPage = repository.findAllWithFilters(status, position, pageable);
    return bannersPage.map(mapper::toDto);
  }

  @Override
  public void delete(Long id) {
    log.info("Xóa banner với ID: {}", id);

    Banner entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Banner", "id", id));

    entity.setStatus(Status.DELETED);
    entity.setUpdatedAt(LocalDateTime.now());

    repository.save(entity);
    log.info("Xóa banner thành công với ID: {}", id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BannerResponseDTO> getActiveBannersByPosition(Integer position) {
    log.info("Lấy danh sách banner hoạt động theo vị trí: {}", position);

    // Query all active banners first, then filter by position in Java
    // This is necessary because position is stored as JSON and JPA queries don't work well with
    // JSON columns
    List<Banner> allActiveBanners = repository.findByStatusOrderByDisplayOrderAsc(Status.ACTIVE);

    List<Banner> banners =
        allActiveBanners.stream()
            .filter(banner -> banner.getPosition() != null && banner.getPosition().equals(position))
            .collect(Collectors.toList());

    log.info(
        "Tìm thấy {} banners với position = {} và status = {} (từ tổng số {} banners ACTIVE)",
        banners.size(),
        position,
        Status.ACTIVE.getValue(),
        allActiveBanners.size());

    return banners.stream().map(mapper::toDto).collect(Collectors.toList());
  }
}
