package com.example.nikonbe.common.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends BaseException {
  public ResourceAlreadyExistsException(String message) {
    super(message, HttpStatus.CONFLICT.value());
  }
}
