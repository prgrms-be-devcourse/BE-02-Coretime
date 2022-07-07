package com.prgrms.coretime.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfig {

  private String header;

  private String refreshHeader;

  private String issuer;

  private String clientSecret;

  private int expirySeconds;

  private int refreshExpirySeconds;

  @Value("${jwt.blacklist.access-token}")
  private String blackListPrefix;
}
