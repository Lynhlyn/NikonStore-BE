package com.example.nikonbe.config.security;

import com.example.nikonbe.common.utils.JWTUtil;
import com.example.nikonbe.security.service.provider.StaffDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class StaffJWTAuthenticationFilter extends OncePerRequestFilter {

  private final JWTUtil jwtUtil;
  private final StaffDetailService staffDetailService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String requestPath = request.getServletPath();
    if (!requestPath.startsWith("/api/v1/admin/")) {
      filterChain.doFilter(request, response);
      return;
    }

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;

    if (StringUtils.isEmpty(authHeader) || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    jwt = authHeader.substring(7);

    try {
      username = jwtUtil.extractUsername(jwt);

      if (StringUtils.isNotEmpty(username)
          && SecurityContextHolder.getContext().getAuthentication() == null) {

        UserDetails userDetails = staffDetailService.loadUserByUsername(username);

        if (jwtUtil.isTokenValid(jwt, userDetails)) {
          SecurityContext context = SecurityContextHolder.createEmptyContext();
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          context.setAuthentication(authToken);
          SecurityContextHolder.setContext(context);

          log.debug("Set authentication for staff: {}", username);
        }
      }
    } catch (UsernameNotFoundException e) {
      log.warn("Staff not found for JWT token: {}", e.getMessage());
    } catch (Exception e) {
      log.error("Error processing JWT token: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }
}
