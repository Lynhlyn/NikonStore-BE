package com.example.nikonbe.common.exceptions;

import org.springframework.http.HttpStatus;

public class BusinessException extends BaseException {
  public BusinessException(String message) {
    super(message, HttpStatus.BAD_REQUEST.value());
  }
}
