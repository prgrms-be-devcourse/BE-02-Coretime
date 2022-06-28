package com.prgrms.coretime.common.jwt;

import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.service.UserService;
import java.util.Arrays;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final Jwt jwt;

  private final UserService userService;

  public JwtAuthenticationProvider(Jwt jwt, UserService userService) {
    this.jwt = jwt;
    this.userService = userService;
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
      String token = getToken(user.getId(), user.getSchool().getId(), user.getNickname(), user.getEmail(), authorities);
      JwtAuthenticationToken authenticated = new JwtAuthenticationToken(new JwtPrincipal(token, user.getNickname(), user.getEmail(), user.getId(), user.getSchool().getId()), null, authorities);
      authenticated.setDetails(user);
      return authenticated;
    } catch (IllegalArgumentException e) {
      throw new BadCredentialsException(e.getMessage());
    } catch (DataAccessException e) {
      throw new AuthenticationServiceException(e.getMessage(), e);
    }
  }

  private String getToken(Long userId, Long schoolId, String nickname, String email, List<GrantedAuthority> authorities) {
    String[] roles = authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .toArray(String[]::new);
    return jwt.sign(Jwt.Claims.from(userId, schoolId, nickname, email, roles));
  }
}
