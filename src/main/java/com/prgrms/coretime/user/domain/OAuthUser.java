package com.prgrms.coretime.user.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("OAUTH")
public class OAuthUser extends User{

  @Column(name = "provider")
  private String provider;

  @Column(name = "provider_id")
  private String providerId;
}
