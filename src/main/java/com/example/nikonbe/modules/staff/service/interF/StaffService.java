package com.example.nikonbe.modules.staff.service.interF;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.common.enums.UserRole;
import com.example.nikonbe.modules.staff.dto.request.StaffCreateDTO;
import com.example.nikonbe.modules.staff.dto.request.StaffUpdateDTO;
import com.example.nikonbe.modules.staff.dto.response.StaffResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StaffService {

  StaffResponseDTO create(StaffCreateDTO dto);

  StaffResponseDTO update(Integer id, StaffUpdateDTO dto);

  StaffResponseDTO getById(Integer id);

  List<StaffResponseDTO> getAll(String fullName, String phoneNumber, UserRole role, Status status);

  Page<StaffResponseDTO> getAllPaginated(Pageable pageable);

  Page<StaffResponseDTO> getAllPaginated(
      String fullName, String phoneNumber, UserRole role, Status status, Pageable pageable);

  void delete(Integer id);

  void hardDelete(Integer id);
}
