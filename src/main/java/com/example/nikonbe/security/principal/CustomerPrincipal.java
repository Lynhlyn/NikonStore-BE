package com.example.nikonbe.security.principal;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.customer.entity.Customer;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

/** Custom User Principal để implement cả UserDetails và OAuth2User */
@Getter
@AllArgsConstructor
public class CustomerPrincipal implements OAuth2User, UserDetails {
  private Integer id;
  private String email;
  private String password;
  private String fullName;
  private Status status;
  private Collection<? extends GrantedAuthority> authorities;
  private Map<String, Object> attributes;

  public static CustomerPrincipal create(Customer customer) {
    Collection<? extends GrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority("USER"));

    return new CustomerPrincipal(
        customer.getId(),
        customer.getEmail(),
        customer.getPassword(),
        customer.getFullName(),
        customer.getStatus(),
        authorities,
        null);
  }

  public static CustomerPrincipal create(Customer customer, Map<String, Object> attributes) {
    CustomerPrincipal customerPrincipal = CustomerPrincipal.create(customer);
    customerPrincipal.attributes = attributes;
    return customerPrincipal;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    // Tài khoản bị khóa nếu status là BLOCKED
    return !Status.BLOCKED.equals(status);
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    // Tài khoản được kích hoạt nếu status là ACTIVE
    return Status.ACTIVE.equals(status);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public String getName() {
    return String.valueOf(id);
  }
}
