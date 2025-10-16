package com.example.nikonbe.common.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BaseException {
  public InvalidTokenException(String message) {
    super(message, HttpStatus.BAD_REQUEST.value());
  }
}
