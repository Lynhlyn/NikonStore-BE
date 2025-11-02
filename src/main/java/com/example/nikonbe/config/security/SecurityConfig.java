package com.example.nikonbe.config.security;

import com.example.nikonbe.common.enums.UserRole;
import com.example.nikonbe.security.service.provider.CustomerDetailService;
import com.example.nikonbe.security.service.provider.StaffDetailService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CorsConfigurationSource corsConfigurationSource;
  private final StaffJWTAuthenticationFilter staffJWTAuthenticationFilter;
  private final CustomerJWTAuthenticationFilter customerJWTAuthenticationFilter;
  private final StaffDetailService staffDetailService;
  private final CustomerDetailService customerDetailService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/api/v1/admin/auth/login",
                        "/api/v1/admin/auth/forgot-password",
                        "/api/v1/admin/auth/reset-password",
                        "/api/v1/admin/auth/validate-reset-token")
                    .permitAll()
                    .requestMatchers("/api/v1/admin/**")
                    .hasAnyAuthority(UserRole.main.name(), UserRole.staff.name())
                    .requestMatchers("/api/v1/**")
                    .permitAll()
                    .anyRequest()
                    .permitAll())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(customerAuthenticationProvider())
        .authenticationProvider(staffAuthenticationProvider())
        .addFilterBefore(customerJWTAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(staffJWTAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider customerAuthenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(customerDetailService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationProvider staffAuthenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(staffDetailService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  @Primary
  public AuthenticationManager authenticationManager() throws Exception {
    return new ProviderManager(List.of(customerAuthenticationProvider()));
  }

  @Bean("staffAuthenticationManager")
  public AuthenticationManager staffAuthenticationManager() throws Exception {
    DaoAuthenticationProvider staffProvider = new DaoAuthenticationProvider();
    staffProvider.setUserDetailsService(staffDetailService);
    staffProvider.setPasswordEncoder(passwordEncoder());
    return new ProviderManager(List.of(staffProvider));
  }
}
