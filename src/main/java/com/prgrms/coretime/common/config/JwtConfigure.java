package com.prgrms.coretime.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfigure {

  private String header;

  private String issuer;

  private String clientSecret;

  private int expirySeconds;
}
