package com.prgrms.coretime.user.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRegisterRequest {

  @NotNull
  private final Long schoolId;

  @NotBlank
  private final String email;

  private final String profileImage;

  @NotBlank
  private final String nickname;

  @NotBlank
  private final String name;

  @NotBlank
  private final String password;
}
