package com.example.nikonbe.common.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
  public ResourceNotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND.value());
  }

  public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
    super(
        String.format("%s không tìm thấy với %s: '%s'", resourceName, fieldName, fieldValue),
        HttpStatus.NOT_FOUND.value());
  }
}
