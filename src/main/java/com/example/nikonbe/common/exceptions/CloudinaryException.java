package com.example.nikonbe.common.exceptions;

import org.springframework.http.HttpStatus;

public class CloudinaryException extends BaseException {
  public CloudinaryException(String message) {
    super(message, HttpStatus.INTERNAL_SERVER_ERROR.value());
  }
}
