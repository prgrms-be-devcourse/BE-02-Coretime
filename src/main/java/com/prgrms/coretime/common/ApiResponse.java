package com.prgrms.coretime.common;

import lombok.Getter;

@Getter
public class ApiResponse {
  private String message;
  private Object data;

  public ApiResponse(String message, Object data) {
    this.message = message;
    this.data = data;
  }
}
