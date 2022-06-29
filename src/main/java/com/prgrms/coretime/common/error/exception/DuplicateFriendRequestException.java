package com.prgrms.coretime.common.error.exception;

import com.prgrms.coretime.common.ErrorCode;
import lombok.Getter;

@Getter
public class DuplicateFriendRequestException extends RuntimeException{

  private final ErrorCode errorCode;

  public DuplicateFriendRequestException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
