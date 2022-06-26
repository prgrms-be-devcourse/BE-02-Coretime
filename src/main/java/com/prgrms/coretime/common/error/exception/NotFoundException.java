package com.prgrms.coretime.common.error.exception;

public class NotFoundException extends RuntimeException{
  public NotFoundException() {
    super();
  }

  public NotFoundException(String message) {
    super(message);
  }
}
