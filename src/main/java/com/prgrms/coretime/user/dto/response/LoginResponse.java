package com.prgrms.coretime.user.dto.response;

import lombok.Getter;

@Getter
public class LoginResponse {

  private final String accessToken;

  private final String refreshToken;

  private final Boolean isLocal;

  public LoginResponse(String accessToken, String refreshToken, Boolean isLocal) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.isLocal = isLocal;
  }
}
