package com.prgrms.coretime.user.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserPasswordChangeRequest {

  @NotBlank
  private final String newPassword;

  @NotBlank
  private final String password;

  public UserPasswordChangeRequest(String newPassword, String password) {
    this.newPassword = newPassword;
    this.password = password;
  }
}
