package com.prgrms.coretime.user.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("LOCAL")
public class LocalUser extends User {

  @Column(name = "password")
  private String password;
}
