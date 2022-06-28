package com.prgrms.coretime.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
  /**
   * Friend Domain
   */
  INVALID_FRIEND_REQUEST_TARGET("F001", "올바르지 않은 친구 상대입니다."),
  DUPLICATE_FRIEND_REQUEST("F002", "이미 완료된 친구 요청입니다."),
  FRIEND_ALREADY_EXISTS("F003", "이미 등록된 친구입니다.");

  private final String code;
  private final String message;
}
