package com.prgrms.coretime.common.util;

import static com.prgrms.coretime.common.ErrorCode.*;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.prgrms.coretime.common.config.JwtConfig;
import com.prgrms.coretime.common.error.exception.AuthErrorException;
import com.prgrms.coretime.common.jwt.Jwt;
import com.prgrms.coretime.common.jwt.claim.AccessClaim;
import com.prgrms.coretime.common.jwt.claim.RefreshClaim;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

  private final Jwt jwt;

  private final JwtConfig jwtConfig;

  private final RedisService redisService;

  public JwtService(Jwt jwt, JwtConfig jwtConfig,
      RedisService redisService) {
    this.jwt = jwt;
    this.jwtConfig = jwtConfig;
    this.redisService = redisService;
  }

  public String createAccessToken(Long userId, Long schoolId, String nickname, String email, List<GrantedAuthority> authorities) {
    String[] roles = authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .toArray(String[]::new);
    return jwt.sign(new AccessClaim(userId, schoolId, nickname, email, roles));
  }

  public String createRefreshToken(String email) {
    String refreshToken = jwt.sign(new RefreshClaim(email));
    redisService.setValues(email, refreshToken, Duration.ofMillis(
        jwtConfig.getRefreshExpirySeconds()));
    return refreshToken;
  }

  public void checkRefreshToken(String email, String refreshToken) {
    String redisToken = redisService.getValues(email);
    if(!redisToken.equals(refreshToken)) {
      throw new AuthErrorException(INVALID_TOKEN_REQUEST);
    }
  }

  public void logout(String token) {
    AccessClaim claim = jwt.decodeAccessToken(token);
    long expiredAccessTokenTime = claim.getExp().getTime() - new Date().getTime();
    redisService.setValues(jwtConfig.getBlackListPrefix() + token, claim.getEmail(), Duration.ofMillis(expiredAccessTokenTime));
    redisService.deleteValues(claim.getEmail());
  }

  public AccessClaim verifyAccessToken(String token) throws JWTVerificationException {
    String expiredAt = redisService.getValues(jwtConfig.getBlackListPrefix() + token);
    if (expiredAt != null) throw new AuthErrorException(TOKEN_EXPIRED);
    return jwt.decodeAccessToken(token);
  }
}
