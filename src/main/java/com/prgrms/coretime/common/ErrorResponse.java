package com.prgrms.coretime.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

  private int status;
  private String code;
  private String message;

  /**
   * status 넣은 이유 : ErrorCode에서 Status와 code 한 번에 관리하기 위해서. 안그러면 ResponseEntity에서 status 따로, body 따로
   * 쓰게 될 것임.. Global Exception Handler 참고 부탁
   */
  private ErrorResponse(int status, String code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }

  public static ErrorResponse of(ErrorCode errorCode) {
    return new ErrorResponse(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage());
  }

}
