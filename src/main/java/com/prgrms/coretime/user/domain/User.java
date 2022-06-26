package com.prgrms.coretime.user.domain;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.friend.domain.Friend;
import com.prgrms.coretime.message.domain.Message;
import com.prgrms.coretime.message.domain.MessageRoom;
import com.prgrms.coretime.school.domain.School;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Getter
public class User extends BaseEntity {

  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "school_id")
  private School school;

  @Column(name = "email", unique = true)
  private String email;

  @Column(name = "profile_image")
  private String profileImage;

  @Column(name = "nickname", unique = true)
  private String nickname;

  @Column(name = "name")
  private String name;

  public User (School school, String email, String profileImage, String nickname, String name) {
    this.school = school;
    this.email = email;
    this.profileImage = profileImage;
    this.nickname = nickname;
    this.name = name;
  }
}
