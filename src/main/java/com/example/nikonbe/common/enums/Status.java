package com.example.nikonbe.common.enums;

public enum Status {
  INACTIVE(0),
  ACTIVE(1),
  PENDING(2),
  DELETED(3);

  private final int value;

  Status(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static Status fromValue(int value) {
    for (Status status : Status.values()) {
      if (status.getValue() == value) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown status value: " + value);
  }
}
