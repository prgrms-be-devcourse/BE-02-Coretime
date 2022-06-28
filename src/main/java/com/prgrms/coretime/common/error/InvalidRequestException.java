package com.prgrms.coretime.common.error;

public class InvalidRequestException extends RuntimeException {

  public InvalidRequestException() {
  }

  public InvalidRequestException(String message) {
    super(message);
  }
}
