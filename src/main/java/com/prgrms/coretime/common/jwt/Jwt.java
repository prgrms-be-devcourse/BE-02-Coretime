package com.prgrms.coretime.common.jwt;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.prgrms.coretime.common.jwt.claim.AccessClaim;
import com.prgrms.coretime.common.jwt.claim.Claims;
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
    claims.applyToBuilder(builder);
    return  builder.sign(algorithm);
  }

  public AccessClaim decodeAccessToken(String token) throws JWTVerificationException {
    return new AccessClaim(jwtVerifier.verify(token));
  }

}
