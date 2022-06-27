package com.prgrms.coretime.user.dto.response;

import lombok.Getter;

@Getter
public class UserResponse {

  private final String accessToken;

  private final Boolean isLocal;

  public UserResponse(String accessToken, Boolean isLocal) {
    this.accessToken = accessToken;
    this.isLocal = isLocal;
  }
}
