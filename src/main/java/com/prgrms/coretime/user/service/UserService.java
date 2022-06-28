
package com.prgrms.coretime.user.service;

import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static com.prgrms.coretime.common.ErrorCode.*;

@Service
public class UserService {

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;


  public UserService(PasswordEncoder passwordEncoder,
      UserRepository userRepository) {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
  }

  // TODO : oauth 고려, register 부분도
  @Transactional
  public User login(String principal, String credentials) {
    Assert.hasText(principal, "principal이 누락되었습니다.");
    Assert.hasText(credentials, "credentials이 누락되었습니다.");

    LocalUser user = (LocalUser) userRepository.findByEmail(principal).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    user.checkPassword(passwordEncoder, credentials);
    return user;
  }

  // TODO : message 관리
  @Transactional(readOnly = true)
  public User findByEmail(String email) {
    Assert.hasText(email, "email이 누락되었습니다.");
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
  }

  @Transactional
  // TODO : 임시 회원가입 구현. 인증 구현해야 함.
  public User register() {
    return null;
  }

}
