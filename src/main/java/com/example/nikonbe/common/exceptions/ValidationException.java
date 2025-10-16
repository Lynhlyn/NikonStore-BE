package com.example.nikonbe.common.exceptions;

import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

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
