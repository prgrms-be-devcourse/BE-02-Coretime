package com.prgrms.coretime.user.dto.response;

import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.user.domain.User;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponse {
  private final Long userId;

  private final Long schoolId;

  private final String email;

  private final String profileImage;

  private final String nickname;

  private final String name;

  public static RegisterResponse from(User user) {
    return RegisterResponse.builder()
        .userId(user.getId())
        .schoolId(user.getSchool().getId())
        .email(user.getEmail())
        .profileImage(user.getProfileImage())
        .nickname(user.getNickname())
        .name(user.getName())
        .build();
  }
}
