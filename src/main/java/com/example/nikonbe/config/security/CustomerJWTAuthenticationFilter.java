package com.example.nikonbe.config.security;

import com.example.nikonbe.common.utils.JWTUtil;
import com.example.nikonbe.security.service.provider.CustomerDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerJWTAuthenticationFilter extends OncePerRequestFilter {

  private final JWTUtil jwtUtil;
  private final CustomerDetailService customerDetailService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    String requestPath = request.getServletPath();
    if (requestPath.startsWith("/api/v1/admin/")) {
      filterChain.doFilter(request, response);
      return;
    }

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String userEmail;

    if (StringUtils.isEmpty(authHeader) || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    jwt = authHeader.substring(7);
    try {
      userEmail = jwtUtil.extractUsername(jwt);
    } catch (ExpiredJwtException ex) {
      handleExpiredToken(response, ex);
      return;
    } catch (Exception ex) {
      filterChain.doFilter(request, response);
      return;
    }

    if (StringUtils.isNotEmpty(userEmail)
        && SecurityContextHolder.getContext().getAuthentication() == null) {

      try {
        UserDetails userDetails = customerDetailService.loadUserByUsername(userEmail);
        if (jwtUtil.isTokenValid(jwt, userDetails)) {
          SecurityContext context = SecurityContextHolder.createEmptyContext();
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          context.setAuthentication(authToken);
          SecurityContextHolder.setContext(context);

          log.debug("Set authentication for customer: {}", userEmail);
        }
      } catch (ExpiredJwtException ex) {
        handleExpiredToken(response, ex);
        return;
      } catch (Exception e) {
        log.warn("Error processing JWT token for customer: {}", e.getMessage());
      }
    }

    filterChain.doFilter(request, response);
  }

  private void handleExpiredToken(HttpServletResponse response, ExpiredJwtException ex)
      throws IOException {
    log.debug("Customer JWT expired: {}", ex.getMessage());
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");
    var body =
        java.util.Map.of(
            "error", "TOKEN_EXPIRED", "message", "Access token đã hết hạn", "status", 401);
    new ObjectMapper().writeValue(response.getWriter(), body);
  }
}

