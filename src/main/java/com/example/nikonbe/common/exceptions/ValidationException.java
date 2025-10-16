package com.example.nikonbe.common.exceptions;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ValidationException extends BaseException {
  private final Map<String, String> errors;

  public ValidationException(String message, Map<String, String> errors) {
    super(message, HttpStatus.UNPROCESSABLE_ENTITY.value());
    this.errors = errors;
  }

  public ValidationException(String message) {
    super(message, HttpStatus.UNPROCESSABLE_ENTITY.value());
    this.errors = null;
  }
}
