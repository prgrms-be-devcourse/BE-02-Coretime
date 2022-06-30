package com.prgrms.coretime.common.jwt;

import com.prgrms.coretime.common.jwt.claim.AccessClaim;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

public class JwtAuthenticationFilter extends GenericFilterBean {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final String accessHeaderKey;

  private final Jwt jwt;

  public JwtAuthenticationFilter(String accessHeaderKey, Jwt jwt) {
    this.accessHeaderKey = accessHeaderKey;
    this.jwt = jwt;
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      String token = getAccessToken(request);
      if (token != null) {
        try {
          AccessClaim claims = jwt.verifyAccessToken(token);
          log.debug("Jwt parse result: {}", claims);

          String nickname = claims.getNickname();
          String email = claims.getEmail();
          Long userId = claims.getUserId();
          Long schoolId= claims.getSchoolId();
          List<GrantedAuthority> authorities = getAuthorities(claims);

          if ((nickname != null && !nickname.trim().equals(""))
              && userId != null
              && authorities.size() > 0
          ) {
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(new JwtPrincipal(token, nickname, email, userId, schoolId), null,authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
        } catch (Exception e) {
          log.warn("Jwt processing failed: {}", e.getMessage());
        }
      }
    } else {
      log.debug("SecurityContextHolder not populated with security token, as it already contained: '{}'", SecurityContextHolder.getContext().getAuthentication());
    }

    chain.doFilter(request, response);
  }

  private String getAccessToken(HttpServletRequest request) {
    String token = request.getHeader(accessHeaderKey);
    if(token != null && !token.trim().equals("")) {
      log.debug("Jwt authorization api detected: {}", token);
      try {
        return URLDecoder.decode(token, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        log.error(e.getMessage(), e);
      }
    }
    return null;
  }

  private List<GrantedAuthority> getAuthorities(AccessClaim claims) {
    String[] roles = claims.getRoles();
    return roles == null || roles.length == 0 ? Collections.emptyList() : Arrays.stream(roles).map(
        SimpleGrantedAuthority::new).collect(Collectors.toList());
  }
}
