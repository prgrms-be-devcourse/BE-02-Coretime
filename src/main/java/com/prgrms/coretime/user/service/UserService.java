
package com.prgrms.coretime.user.service;

import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class UserService {

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;


  public UserService(PasswordEncoder passwordEncoder,
      UserRepository userRepository) {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
  }

  // TODO : oauth 고려
  public User login(String principal, String credentials) {
    Assert.hasText(principal, "principal이 누락되었습니다.");
    Assert.hasText(credentials, "credentials이 누락되었습니다.");

    LocalUser user = (LocalUser) userRepository.findByEmail(principal).orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다."));
    user.checkPassword(passwordEncoder, credentials);
    return user;
  }

  // TODO : message 관리
  @Transactional(readOnly = true)
  public User findByEmail(String email) {
    Assert.hasText(email, "email이 누락되었습니다.");
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다."));
  }

}
