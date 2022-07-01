package com.prgrms.coretime.common.error.exception;

import com.prgrms.coretime.common.ErrorCode;
import lombok.Getter;

@Getter
public class LikeAlreadyExistsException extends RuntimeException {

  private final ErrorCode errorCode;

  public LikeAlreadyExistsException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
