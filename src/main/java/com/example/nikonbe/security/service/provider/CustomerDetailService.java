package com.example.nikonbe.security.service.provider;

import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.customer.repository.CustomerRepository;
import com.example.nikonbe.security.principal.CustomerPrincipal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerDetailService implements UserDetailsService {

  private final CustomerRepository customerRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Customer> customer = customerRepository.findByEmailOrUsername(username);
    if (customer.isEmpty()) {
      throw new UsernameNotFoundException("User not found");
    }

    Customer cus = customer.get();
    return CustomerPrincipal.create(cus);
  }
}
