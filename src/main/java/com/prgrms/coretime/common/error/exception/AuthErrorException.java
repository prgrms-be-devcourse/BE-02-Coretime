package com.prgrms.coretime.common.error.exception;

import com.prgrms.coretime.common.ErrorCode;

public class AuthErrorException extends RuntimeException {

  private final ErrorCode errorCode;

  public AuthErrorException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
