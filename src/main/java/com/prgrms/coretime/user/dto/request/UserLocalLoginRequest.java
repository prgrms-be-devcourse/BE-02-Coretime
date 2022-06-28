package com.prgrms.coretime.user.dto.request;

import lombok.Getter;

@Getter
public class UserLocalLoginRequest {
  private final String email;

  private final String password;

  public UserLocalLoginRequest(String email, String password) {
    this.email = email;
    this.password = password;
  }
}
