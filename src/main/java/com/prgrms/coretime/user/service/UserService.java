
package com.prgrms.coretime.user.service;

import com.prgrms.coretime.common.error.exception.AlreadyExistsException;
import com.prgrms.coretime.common.error.exception.AuthErrorException;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import com.prgrms.coretime.user.dto.request.UserPasswordChangeRequest;
import com.prgrms.coretime.user.dto.request.UserRegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static com.prgrms.coretime.common.ErrorCode.*;

@Service
public class UserService {

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  private final SchoolRepository schoolRepository;

  public UserService(PasswordEncoder passwordEncoder,
      UserRepository userRepository,
      SchoolRepository schoolRepository) {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
    this.schoolRepository = schoolRepository;
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

  @Transactional(readOnly = true)
  public User findByEmail(String email) {
    Assert.hasText(email, "email이 누락되었습니다.");
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public User findById(Long userId) {
    Assert.notNull(userId, "userId가 누락되었습니다.");
    return userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public User findByNickname(String nickname) {
    Assert.hasText(nickname, "nickname이 누락되었습니다.");
    return userRepository.findByNickname(nickname)
        .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public boolean checkNicknameUnique(String nickname) {
    return userRepository.existsByNickname(nickname);
  }

  @Transactional
  public User register(UserRegisterRequest request) {
    School school = schoolRepository.findById(request.getSchoolId()).orElseThrow(() -> new NotFoundException(SCHOOL_NOT_FOUND));
    if(userRepository.existsByEmail(request.getEmail())) throw new AlreadyExistsException(USER_ALREADY_EXISTS);
    if(userRepository.existsByNickname(request.getNickname())) throw new AlreadyExistsException(USER_ALREADY_EXISTS);

    authenticateUserEmail(request.getEmail(), school);
    User newUser = LocalUser.builder()
        .name(request.getName())
        .nickname(request.getNickname())
        .email(request.getEmail())
        .profileImage(request.getProfileImage())
        .school(school)
        .password(passwordEncoder.encode(request.getPassword()))
        .build();
    return userRepository.save(newUser);
  }

  private void authenticateUserEmail(String userEmail, School school) {
    String[] parsed = userEmail.split("@");
    if(!school.getEmail().equals(parsed[1])) {
      throw new AuthErrorException(SCHOOL_AUTH_FAILED);
    }
  }

  public void changePassword(LocalUser user, UserPasswordChangeRequest request) {
    user.checkPassword(passwordEncoder, request.getPassword());
    user.changePassword(request.getNewPassword());
  }

}
