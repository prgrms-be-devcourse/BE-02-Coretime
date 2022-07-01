package com.prgrms.coretime.common.util;

import com.prgrms.coretime.common.ErrorCode;
import com.prgrms.coretime.common.config.JwtConfig;
import com.prgrms.coretime.common.error.exception.AuthErrorException;
import com.prgrms.coretime.common.jwt.Jwt;
import com.prgrms.coretime.common.jwt.claim.AccessClaim;
import com.prgrms.coretime.common.jwt.claim.RefreshClaim;
import java.time.Duration;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final JwtConfig jwtConfig;

  private final RedisService redisService;

  public JwtService(JwtConfig jwtConfig, RedisService redisService) {
    this.jwtConfig = jwtConfig;
    this.redisService = redisService;
  }

  public String createAccessToken(Long userId, Long schoolId, String nickname, String email, List<GrantedAuthority> authorities) {
    String[] roles = authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .toArray(String[]::new);
    Jwt jwt = new Jwt(jwtConfig.getIssuer(), jwtConfig.getClientSecret(),
        jwtConfig.getExpirySeconds());
    return jwt.sign(new AccessClaim(userId, schoolId, nickname, email, roles));
  }

  public String createRefreshToken(String email) {
    Jwt jwt = new Jwt(jwtConfig.getIssuer(), jwtConfig.getClientSecret(),
        jwtConfig.getRefreshExpirySeconds());
    String refreshToken = jwt.sign(new RefreshClaim(email));
    redisService.setValues(email, refreshToken, Duration.ofMillis(
        jwtConfig.getRefreshExpirySeconds()));
    return refreshToken;
  }

  public void checkRefreshToken(String email, String refreshToken) {
    String redisToken = redisService.getValues(email);
    if(!redisToken.equals(refreshToken)) {
      throw new AuthErrorException(ErrorCode.INVALID_TOKEN_REQUEST);
    }
  }
}
