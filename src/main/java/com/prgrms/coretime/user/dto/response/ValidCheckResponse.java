package com.prgrms.coretime.user.dto.response;

import lombok.Getter;

@Getter
public class ValidCheckResponse {

  private final Boolean isValid;

  public ValidCheckResponse(Boolean isValid) {
    this.isValid = isValid;
  }
}
