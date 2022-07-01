package com.prgrms.coretime.common.jwt;

import org.springframework.util.Assert;

public class JwtPrincipal {

  public final String accessToken;

  public final String nickname;

  public final String email;

  public final Long userId;

  public final Long schoolId;

  JwtPrincipal(String accessToken, String nickname,
      String email,
      Long userId, Long schoolId) {
    Assert.hasText(accessToken, "accessToken이 공백이거나 누락되었습니다.");
    Assert.hasText(nickname, "nickname이 공백이거나 누락되었습니다.");
    Assert.hasText(email, "email 공백이거나 누락되었습니다.");
    Assert.notNull(userId,"userId가 누락되었습니다.");
    Assert.notNull(schoolId,"schoolId가 누락되었습니다.");

    this.accessToken = accessToken;
    this.nickname = nickname;
    this.email = email;
    this.userId = userId;
    this.schoolId = schoolId;
  }
}
