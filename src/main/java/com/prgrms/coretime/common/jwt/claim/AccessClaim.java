package com.prgrms.coretime.common.jwt.claim;

import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import lombok.Getter;

@Getter
public class AccessClaim implements Claims {
  private Long userId;
  private Long schoolId;
  private String nickname;
  private String email;
  private String[] roles;
  private Date iat; // 발행 시각
  private Date exp; // 만료 시각

  public AccessClaim(Long userId, Long schoolId, String nickname, String email, String[] roles){
    this.userId = userId;
    this.schoolId = schoolId;
    this.nickname = nickname;
    this.email = email;
    this.roles = roles;
  };

  public AccessClaim(DecodedJWT decodedJWT) {
    Claim userId = decodedJWT.getClaim("userId");
    if (!userId.isNull()) {
      this.userId = userId.asLong();
    }
    Claim schoolId = decodedJWT.getClaim("schoolId");
    if (!schoolId.isNull()) {
      this.schoolId = schoolId.asLong();
    }
    Claim nickname = decodedJWT.getClaim("nickname");
    if (!nickname.isNull()) {
      this.nickname = nickname.asString();
    }
    Claim email = decodedJWT.getClaim("email");
    if (!email.isNull()) {
      this.email = email.asString();
    }
    Claim roles = decodedJWT.getClaim("roles");
    if (!roles.isNull()) {
      this.roles = roles.asArray(String.class);
    }
    this.iat = decodedJWT.getIssuedAt();
    this.exp = decodedJWT.getExpiresAt();
  }

  @Override
  public void applyToBuilder(Builder builder) {
    builder.withClaim("userId", this.userId);
    builder.withClaim("nickname", this.nickname);
    builder.withClaim("email", this.email);
    builder.withClaim("schoolId", this.schoolId);
    builder.withArrayClaim("roles", this.roles);
  }
}
