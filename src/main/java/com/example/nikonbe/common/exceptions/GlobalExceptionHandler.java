package com.example.nikonbe.common.exceptions;

import com.example.nikonbe.common.response.ApiResponseDto;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleBaseException(BaseException ex) {
    log.error("Base exception: {}", ex.getMessage());
    return buildResponseEntity(ex.getStatus(), ex.getMessage(), null);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleResourceNotFoundException(
      ResourceNotFoundException ex) {
    log.error("Resource not found: {}", ex.getMessage());
    return buildResponseEntity(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
  }

  @ExceptionHandler(ResourceAlreadyExistsException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleResourceAlreadyExistsException(
      ResourceAlreadyExistsException ex) {
    log.error("Resource already exists: {}", ex.getMessage());
    return buildResponseEntity(HttpStatus.CONFLICT.value(), ex.getMessage(), null);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleValidationException(ValidationException ex) {
    log.error("Validation error: {}", ex.getMessage());
    return buildResponseEntity(
        HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage(), ex.getErrors());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    log.error("Method argument validation error: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    return buildResponseEntity(HttpStatus.BAD_REQUEST.value(), "Xác thực dữ liệu thất bại", errors);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleConstraintViolationException(
      ConstraintViolationException ex) {
    log.error("Constraint violation: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getConstraintViolations()
        .forEach(
            violation -> {
              String propertyPath = violation.getPropertyPath().toString();
              String field =
                  propertyPath.contains(".")
                      ? propertyPath.substring(propertyPath.lastIndexOf('.') + 1)
                      : propertyPath;
              errors.put(field, violation.getMessage());
            });

    return buildResponseEntity(HttpStatus.BAD_REQUEST.value(), "Xác thực dữ liệu thất bại", errors);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex) {
    log.error("Message not readable: {}", ex.getMessage());
    return buildResponseEntity(HttpStatus.BAD_REQUEST.value(), "Yêu cầu JSON không hợp lệ", null);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
    log.error("Type mismatch: {}", ex.getMessage());
    return buildResponseEntity(
        HttpStatus.BAD_REQUEST.value(), "Tham số không hợp lệ: " + ex.getName(), null);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex) {
    log.error("Data integrity violation: {}", ex.getMessage());
    return buildResponseEntity(
        HttpStatus.CONFLICT.value(), "Vi phạm ràng buộc cơ sở dữ liệu", null);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleBadCredentialsException(
      BadCredentialsException ex) {
    log.warn("Authentication failed: bad credentials");
    return buildResponseEntity(
        HttpStatus.UNAUTHORIZED.value(), "Tên người dùng hoặc mật khẩu không đúng", null);
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleDisabledException(DisabledException ex) {
    log.warn("Authentication failed: account disabled");
    return buildResponseEntity(HttpStatus.FORBIDDEN.value(), "Tài khoản đã bị khóa", null);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleUsernameNotFoundException(
      UsernameNotFoundException ex) {
    log.warn("Authentication failed: username not found");
    return buildResponseEntity(HttpStatus.NOT_FOUND.value(), "Tài khoản không tồn tại", null);
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleInvalidTokenException(
      InvalidTokenException ex) {
    log.warn("Invalid token: {}", ex.getMessage());
    return buildResponseEntity(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
  }

  @ExceptionHandler(InvalidOperationException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleInvalidOperationException(
      InvalidOperationException ex) {
    log.warn("Invalid operation: {}", ex.getMessage());
    return buildResponseEntity(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleBusinessException(BusinessException ex) {
    log.error("Business exception: {}", ex.getMessage());
    return buildResponseEntity(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseDto<Object>> handleGenericException(Exception ex) {
    log.error("Unhandled exception: ", ex);
    return buildResponseEntity(
        HttpStatus.INTERNAL_SERVER_ERROR.value(), "Đã xảy ra lỗi không mong muốn", null);
  }

  private ResponseEntity<ApiResponseDto<Object>> buildResponseEntity(
      int status, String message, Object data) {
    ApiResponseDto<Object> response =
        ApiResponseDto.builder().status(status).message(message).data(data).build();
    return ResponseEntity.status(status).body(response);
  }
}
