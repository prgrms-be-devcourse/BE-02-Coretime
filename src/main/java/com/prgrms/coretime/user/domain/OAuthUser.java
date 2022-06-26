package com.prgrms.coretime.user.domain;

import com.prgrms.coretime.school.domain.School;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@DiscriminatorValue("OAUTH")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthUser extends User{

  @Column(name = "provider")
  private String provider;

  @Column(name = "provider_id")
  private String providerId;

  @Builder
  public OAuthUser(School school, String email, String profileImage, String nickname, String name, String provider, String providerId) {
    super(school, email, profileImage, nickname, name);
    this.provider = provider;
    this.providerId = providerId;
  }
}
