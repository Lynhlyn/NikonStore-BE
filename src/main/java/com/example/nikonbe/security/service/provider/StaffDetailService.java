package com.example.nikonbe.security.service.provider;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.staff.entity.Staff;
import com.example.nikonbe.modules.staff.repository.StaffRepository;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service("staffDetailService")
@RequiredArgsConstructor
public class StaffDetailService implements UserDetailsService {

  private final StaffRepository staffRepository;

  @Override
  public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
    log.debug("Loading staff by login: {}", login);

    Staff staff = findStaffByLogin(login);

    if (staff.getStatus() != Status.ACTIVE) {
      log.warn("Staff account is not active: {}", login);
      throw new UsernameNotFoundException("Staff account is not active");
    }

    String roleAuthority = staff.getRole().name();

    log.debug("Staff found: {} with role: {}", staff.getUsername(), staff.getRole());

    return User.builder()
        .username(staff.getUsername())
        .password(staff.getPassword())
        .authorities(Collections.singletonList(new SimpleGrantedAuthority(roleAuthority)))
        .accountExpired(false)
        .accountLocked(staff.getStatus() != Status.ACTIVE)
        .credentialsExpired(false)
        .disabled(staff.getStatus() != Status.ACTIVE)
        .build();
  }

  private Staff findStaffByLogin(String login) throws UsernameNotFoundException {
    Optional<Staff> staffOpt = staffRepository.findByUsername(login);

    if (staffOpt.isEmpty()) {
      staffOpt = staffRepository.findByEmail(login);
    }

    if (staffOpt.isEmpty()) {
      log.warn("Staff not found with login: {}", login);
      throw new UsernameNotFoundException("Staff not found with username or email: " + login);
    }

    return staffOpt.get();
  }
}
