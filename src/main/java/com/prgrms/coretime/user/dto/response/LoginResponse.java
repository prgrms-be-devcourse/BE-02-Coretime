package com.prgrms.coretime.user.dto.response;

import lombok.Getter;

@Getter
public class LoginResponse {

  private final String accessToken;

  private final Boolean isLocal;

  public LoginResponse(String accessToken, Boolean isLocal) {
    this.accessToken = accessToken;
    this.isLocal = isLocal;
  }
}
