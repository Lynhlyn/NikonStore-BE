package com.example.nikonbe.modules.staff.service.impl;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.enums.UserRole;
import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.common.exceptions.ValidationException;
import com.example.nikonbe.modules.staff.dto.request.StaffCreateDTO;
import com.example.nikonbe.modules.staff.dto.request.StaffUpdateDTO;
import com.example.nikonbe.modules.staff.dto.response.StaffResponseDTO;
import com.example.nikonbe.modules.staff.entity.Staff;
import com.example.nikonbe.modules.staff.mapper.StaffMapper;
import com.example.nikonbe.modules.staff.repository.StaffRepository;
import com.example.nikonbe.modules.staff.service.interF.StaffService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StaffServiceImpl implements StaffService {

  private final StaffRepository repository;
  private final StaffMapper mapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public StaffResponseDTO create(StaffCreateDTO dto) {
    log.info("Creating new staff with username: {}", dto.getUsername());

    validateUniqueFieldsForCreate(dto);

    Staff staff = mapper.toEntity(dto);
    staff.setPassword(passwordEncoder.encode(dto.getPassword()));
    staff.setStatus(Status.ACTIVE);
    staff.setRole(UserRole.valueOf(dto.getRole()));

    Staff savedStaff = repository.save(staff);
    log.info("Staff created successfully with ID: {}", savedStaff.getId());

    return mapper.toDto(savedStaff);
  }

  @Override
  public StaffResponseDTO update(Integer id, StaffUpdateDTO dto) {
    log.info("Updating staff with ID: {}", id);
    Staff existingStaff = findStaffById(id);
    validateUniqueFieldsForUpdate(dto, id);
    mapper.updateEntityFromDto(dto, existingStaff);
    existingStaff.setRole(UserRole.valueOf(dto.getRole()));
    existingStaff.setUpdatedAt(LocalDateTime.now());
    Staff updatedStaff = repository.save(existingStaff);
    log.info("Staff updated successfully with ID: {}", id);
    return mapper.toDto(updatedStaff);
  }

  @Override
  @Transactional(readOnly = true)
  public StaffResponseDTO getById(Integer id) {
    Staff staff = findStaffById(id);
    return mapper.toDto(staff);
  }

  @Override
  @Transactional(readOnly = true)
  public List<StaffResponseDTO> getAll(
      String fullName, String phoneNumber, UserRole role, Status status) {
    List<Staff> staffList = repository.findAllByFilters(fullName, phoneNumber, role, status);
    return mapper.toDtoList(staffList);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<StaffResponseDTO> getAllPaginated(Pageable pageable) {
    return repository.findAll(pageable).map(mapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<StaffResponseDTO> getAllPaginated(
      String fullName, String phoneNumber, UserRole role, Status status, Pageable pageable) {
    Page<Staff> staffPage =
        repository.findAllByFiltersPaginated(fullName, phoneNumber, role, status, pageable);
    return staffPage.map(mapper::toDto);
  }

  @Override
  public void delete(Integer id) {
    log.info("Soft deleting staff with ID: {}", id);

    Staff staff = findStaffById(id);
    staff.setStatus(Status.DELETED);
    repository.save(staff);

    log.info("Staff soft deleted successfully with ID: {}", id);
  }

  @Override
  public void hardDelete(Integer id) {
    log.info("Hard deleting staff with ID: {}", id);

    if (!repository.existsById(id)) {
      throw new ResourceNotFoundException("Staff", "id", id);
    }

    repository.deleteById(id);
    log.info("Staff hard deleted successfully with ID: {}", id);
  }

  private Staff findStaffById(Integer id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));
  }

  private void validateUniqueFieldsForCreate(StaffCreateDTO dto) {
    Map<String, String> errors = new HashMap<>();

    if (repository.existsByUsername(dto.getUsername())) {
      errors.put("username", "Username đã tồn tại");
    }

    if (repository.existsByEmail(dto.getEmail())) {
      errors.put("email", "Email đã tồn tại");
    }

    if (repository.existsByPhoneNumber(dto.getPhoneNumber())) {
      errors.put("phoneNumber", "Số điện thoại đã tồn tại");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException("Dữ liệu không hợp lệ", errors);
    }
  }

  private void validateUniqueFieldsForUpdate(StaffUpdateDTO dto, Integer id) {
    Map<String, String> errors = new HashMap<>();

    if (repository.existsByEmailAndIdNot(dto.getEmail(), id)) {
      errors.put("email", "Email đã tồn tại");
    }

    if (repository.existsByPhoneNumberAndIdNot(dto.getPhoneNumber(), id)) {
      errors.put("phoneNumber", "Số điện thoại đã tồn tại");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException("Dữ liệu không hợp lệ", errors);
    }
  }
}
