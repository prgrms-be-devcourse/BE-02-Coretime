package com.prgrms.coretime.common.error;

public class NotFoundException extends RuntimeException {

  public NotFoundException() {
    super();
  }

  public NotFoundException(String message) {
    super(message);
  }
}
