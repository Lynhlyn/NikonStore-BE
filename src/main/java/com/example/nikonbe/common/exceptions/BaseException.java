package com.example.nikonbe.common.exceptions;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
  private final String message;
  private final int status;

  protected BaseException(String message, int status) {
    super(message);
    this.message = message;
    this.status = status;
  }
}
