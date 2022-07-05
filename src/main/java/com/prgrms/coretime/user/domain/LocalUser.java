package com.prgrms.coretime.user.domain;

import static com.prgrms.coretime.common.ErrorCode.*;

import com.prgrms.coretime.common.ErrorCode;
import com.prgrms.coretime.common.error.exception.AuthErrorException;
import com.prgrms.coretime.common.error.exception.InvalidRequestException;
import com.prgrms.coretime.school.domain.School;
import java.util.regex.Pattern;
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
@DiscriminatorValue("LOCAL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalUser extends User {

  private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
  private static final int MAX_PASSWORD_LENGTH = 500;

  @Column(name = "password")
  private String password;

  @Builder
  public LocalUser(School school, String email, String profileImage, String nickname, String name, String password) {
    super(school, email, profileImage, nickname, name);
    this.password = password;
  }

  public void checkPassword(PasswordEncoder passwordEncoder, String credentials) {
    if (!passwordEncoder.matches(credentials, password))
      throw new AuthErrorException(INVALID_ACCOUNT_REQUEST);
  }

  private void validatePassword(String password) {
    if(password.length() > MAX_PASSWORD_LENGTH) {
      throw new InvalidRequestException(INVALID_LENGTH);
    }
    if(!Pattern.matches(PASSWORD_REGEX, password)) {
      throw new InvalidRequestException(INVALID_INPUT_VALUE);
    }
  }

  public void changePassword(PasswordEncoder passwordEncoder, String password) {
    validatePassword(password);
    this.password = passwordEncoder.encode(password);
  }
}
