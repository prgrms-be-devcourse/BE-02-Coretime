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

@Entity
@DiscriminatorValue("LOCAL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalUser extends User {

  @Column(name = "password")
  private String password;

  @Builder
  public LocalUser(School school, String email, String profileImage, String nickname, String name, String password) {
    super(school, email, profileImage, nickname, name);
    this.password = password;
  }

}
