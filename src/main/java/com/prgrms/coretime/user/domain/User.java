package com.prgrms.coretime.user.domain;

import static com.prgrms.coretime.common.ErrorCode.*;

import com.prgrms.coretime.common.error.exception.InvalidRequestException;
import java.util.regex.Pattern;
import org.springframework.util.Assert;
import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.school.domain.School;
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
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Getter
public class User extends BaseEntity {

  private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
  private static final String NAME_REGEX = "[a-zA-Z가-힣]+( [a-zA-Z가-힣]+)*";
  private static final String NICKNAME_REGEX = "[a-zA-Z가-힣0-9]+( [a-zA-Z가-힣0-9]+)*";
  private static final int MAX_EMAIL_LENGTH = 100;
  private static final int MAX_NICKNAME_LENGTH = 10;
  private static final int MAX_NAME_LENGTH = 10;
  private static final int MAX_PROFILEIMAGE_LENGTH = 300;

  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "school_id", nullable = false)
  private School school;

  @Column(name = "email", nullable = false, length = MAX_EMAIL_LENGTH)
  private String email;

  @Column(name = "profile_image", length = 300)
  private String profileImage;

  @Column(name = "nickname", nullable = false, length = MAX_NICKNAME_LENGTH)
  private String nickname;

  @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
  private String name;

  @Column(name = "is_quit")
  private Boolean isQuit = false;

  /*TODO : Refactoring*/
  private static void validateName(String name) {
    if(name.length() > MAX_NAME_LENGTH) {
      throw new InvalidRequestException(INVALID_LENGTH);
    }
    if(!Pattern.matches(NAME_REGEX, name)) {
      throw new InvalidRequestException(INVALID_INPUT_VALUE);
    }
  }

  private static void validateNickname(String nickname) {
    if(nickname.length() > MAX_NICKNAME_LENGTH) {
      throw new InvalidRequestException(INVALID_LENGTH);
    }
    if(!Pattern.matches(NICKNAME_REGEX, nickname)) {
      throw new InvalidRequestException(INVALID_INPUT_VALUE);
    }
  }

  private static void validateEmail(String email) {
    if(email.length() > MAX_EMAIL_LENGTH) {
      throw new InvalidRequestException(INVALID_LENGTH);
    }
    if(!Pattern.matches(EMAIL_REGEX, email)) {
      throw new InvalidRequestException(INVALID_INPUT_VALUE);
    }
  }

  private static void validateProfileImage(String profileImage) {
    if(profileImage.length() > MAX_PROFILEIMAGE_LENGTH) {
      throw new InvalidRequestException(INVALID_LENGTH);
    }
  }

  public User(School school, String email, String profileImage, String nickname, String name) {
    Assert.hasText(email, "email이 누락되었습니다.");
    Assert.hasText(nickname, "nickname이 누락되었습니다.");
    Assert.hasText(name, "user의 name이 누락되었습니다.");
    Assert.notNull(school, "school이 누락되었습니다.");

    validateEmail(email);
    validateName(name);
    validateNickname(nickname);
    if(profileImage != null) validateProfileImage(profileImage);

    this.school = school;
    this.email = email;
    this.profileImage = profileImage;
    this.nickname = nickname;
    this.name = name;
  }

  public User(String email, String name) {
    Assert.hasText(email, "email이 누락되었습니다.");
    Assert.hasText(name, "name이 누락되었습니다.");

    validateEmail(email);
    validateName(name);

    this.email = email;
    this.name = name;
  }

  public void changeQuitFlag(Boolean flag) {
    this.isQuit = flag;
  }
}
