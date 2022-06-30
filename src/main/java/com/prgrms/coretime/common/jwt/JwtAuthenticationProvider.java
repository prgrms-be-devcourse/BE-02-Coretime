package com.prgrms.coretime.common.jwt;

import com.prgrms.coretime.common.config.JwtConfig;
import com.prgrms.coretime.common.jwt.claim.AccessClaim;
import com.prgrms.coretime.common.jwt.claim.RefreshClaim;
import com.prgrms.coretime.common.util.RedisService;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.service.UserService;
import java.time.Duration;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final JwtConfig jwtConfig;

  private final UserService userService;

  private final RedisService redisService;

  public JwtAuthenticationProvider(JwtConfig jwtConfig,
      UserService userService, RedisService redisService) {
    this.jwtConfig = jwtConfig;
    this.userService = userService;
    this.redisService = redisService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    JwtAuthenticationToken jwtAuthentication = (JwtAuthenticationToken) authentication;
    return processUserAuthentication(String.valueOf(jwtAuthentication.getPrincipal()), jwtAuthentication.getCredentials());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    Assert.isAssignable(authentication, JwtAuthenticationToken.class);
    return true;
  }

  private Authentication processUserAuthentication(String principal, String credentials) {
    try{
      User user = userService.login(principal, credentials);
      // TODO : 확장성 고려할 것
      List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
      String accessToken = createAccessToken(user.getId(), user.getSchool().getId(), user.getNickname(), user.getEmail(), authorities);
      String refreshToken = createRefreshToken(user.getEmail());
      redisService.setValues(user.getEmail(), refreshToken, Duration.ofMillis(
          jwtConfig.getRefreshExpirySeconds()));
      JwtAuthenticationToken authenticated = new JwtAuthenticationToken(new JwtPrincipal(accessToken, user.getNickname(), user.getEmail(), user.getId(), user.getSchool().getId()), null, authorities);
      authenticated.setDetails(refreshToken);
      return authenticated;
    } catch (IllegalArgumentException e) {
      throw new BadCredentialsException(e.getMessage());
    } catch (DataAccessException e) {
      throw new AuthenticationServiceException(e.getMessage(), e);
    }
  }

  private String createAccessToken(Long userId, Long schoolId, String nickname, String email, List<GrantedAuthority> authorities) {
    String[] roles = authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .toArray(String[]::new);
    Jwt jwt = new Jwt(jwtConfig.getIssuer(), jwtConfig.getClientSecret(),
        jwtConfig.getExpirySeconds());
    return jwt.sign(new AccessClaim(userId, schoolId, nickname, email, roles));
  }

  private String createRefreshToken(String email) {
    Jwt jwt = new Jwt(jwtConfig.getIssuer(), jwtConfig.getClientSecret(),
        jwtConfig.getRefreshExpirySeconds());
    return jwt.sign(new RefreshClaim(email));
  }
}
