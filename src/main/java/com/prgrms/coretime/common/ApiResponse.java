package com.prgrms.coretime.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;

@Getter
@JsonInclude(Include.NON_NULL)
public class ApiResponse<T> {
  private String message;
  private T data;

  public ApiResponse(String message, T data) {
    this.message = message;
    this.data = data;
  }
}
