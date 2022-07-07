package com.prgrms.coretime.user.domain.repository;

import com.prgrms.coretime.TestConfig;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.OAuthUser;
import com.prgrms.coretime.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static com.prgrms.coretime.common.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(TestConfig.class)
@ActiveProfiles({"test"})
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private SchoolRepository schoolRepository;

  @Test
  @DisplayName("UserRepository를 통해 LocalUser와 OAuthUser instance를 가져올 수 있다.")
  void testFindByEmail() throws Throwable {
    School testSchool = new School("아주대학교", "ajou.ac.kr");
    String localTestEmail = "local@ajou.ac.kr";
    String oauthTestEmail = "oauth@ajou.ac.kr";
    User user1 = LocalUser.builder()
        .nickname("local유저")
        .profileImage("예시 링크")
        .email(localTestEmail)
        .name("김승은로컬")
        .school(testSchool)
        .password("test1234!")
        .build();
    User user2 = OAuthUser.builder()
        .nickname("oauth유저")
        .profileImage("예시 링크")
        .email(oauthTestEmail)
        .name("김승은oauth")
        .school(testSchool)
        .provider("카카오")
        .providerId("카카오id")
        .build();

    schoolRepository.save(testSchool);
    userRepository.save(user1);
    userRepository.save(user2);

    User localResult = userRepository.findByEmailAndIsQuit(localTestEmail, false).orElseThrow(() -> new NotFoundException(
        USER_NOT_FOUND));
    User oauthResult = userRepository.findByEmailAndIsQuit(oauthTestEmail, false).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

    assertThat(localResult).isInstanceOf(LocalUser.class);
    assertThat(oauthResult).isInstanceOf(OAuthUser.class);
    assertThat(localResult.getNickname()).isEqualTo("local유저");
    assertThat(oauthResult.getNickname()).isEqualTo("oauth유저");
  }
}