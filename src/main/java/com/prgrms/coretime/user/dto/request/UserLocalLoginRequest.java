package com.prgrms.coretime.user.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserLocalLoginRequest {
  @NotBlank
  private final String email;

  @NotBlank
  private final String password;

  public UserLocalLoginRequest(String email, String password) {
    this.email = email;
    this.password = password;
  }
}
