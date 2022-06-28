package com.prgrms.coretime.common.jwt;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import lombok.Getter;

/*
 * Jwt
 * - Jwt 토큰의 정보 소유
 * - Jwt 토큰 발행 (sign)
 * - Jwt 토큰 검증 (verify)
 * */
@Getter
public final class Jwt {

  private final String issuer;

  private final String clientSecret;

  private final int expirySeconds;

  private final Algorithm algorithm;

  private final JWTVerifier jwtVerifier;

  public Jwt(String issuer, String clientSecret, int expirySeconds) {
    this.issuer = issuer;
    this.clientSecret = clientSecret;
    this.expirySeconds = expirySeconds;
    this.algorithm = Algorithm.HMAC512(clientSecret);
    this.jwtVerifier = com.auth0.jwt.JWT.require(algorithm)
        .withIssuer(issuer)
        .build();
  }

  public String sign(Claims claims){
    Date now = new Date();
    JWTCreator.Builder builder = com.auth0.jwt.JWT.create();
    builder.withIssuer(issuer);
    builder.withIssuedAt(now);
    if(expirySeconds > 0) {
      builder.withExpiresAt(new Date(now.getTime() + expirySeconds * 1_000L));
    }
    builder.withClaim("userId", claims.userId);
    builder.withClaim("nickname", claims.nickname);
    builder.withClaim("email", claims.email);
    builder.withClaim("schoolId", claims.schoolId);
    builder.withArrayClaim("roles", claims.roles);
    return  builder.sign(algorithm);
  }

  public Claims verify(String token) throws JWTVerificationException {
    return new Claims(jwtVerifier.verify(token));
  }

  static public class Claims {

    Long userId;
    Long schoolId;
    String nickname;
    String email;
    String[] roles;
    Date iat; // 발행 시각
    Date exp; // 만료 시각

    private Claims() {/*no-ops*/}

    Claims(DecodedJWT decodedJWT) {
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

    public static Claims from(Long userId, Long schoolId, String nickname, String email, String[] roles) {
      Claims claims = new Claims();
      claims.userId = userId;
      claims.schoolId = schoolId;
      claims.nickname = nickname;
      claims.email = email;
      claims.roles = roles;
      return claims;
    }
  }

}
