package com.prgrms.coretime.user.domain;

import com.prgrms.coretime.common.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "test_users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Getter
public class TestUser extends BaseEntity {

  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "nickname")
  private String nickname;

  public TestUser(String nickname) {
    this.nickname = nickname;
  }
}
