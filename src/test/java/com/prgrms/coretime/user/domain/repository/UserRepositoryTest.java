package com.prgrms.coretime.user.domain.repository;

import com.prgrms.coretime.common.error.NotFoundException;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.OAuthUser;
import com.prgrms.coretime.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({"test"})
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private SchoolRepository schoolRepository;

  @Test
  @DisplayName("UserRepository를 통해 LocalUser와 OAuthUser instance를 가져올 수 있다.")
  void testFindByEmail() throws Throwable {
    School testSchool = new School(1L, "아주대학교", "ajou.ac.kr");
    String localTestEmail = "local@ajou.ac.kr";
    String oauthTestEmail = "oauth@ajou.ac.kr";
    User user1 = LocalUser.builder()
        .nickname("로컬유저")
        .profileImage("예시 링크")
        .email(localTestEmail)
        .nickname("빈푸")
        .name("김승은로컬")
        .school(testSchool)
        .password("test1234!")
        .build();
    User user2 = OAuthUser.builder()
        .nickname("oauthuser")
        .profileImage("예시 링크")
        .nickname("자바빈")
        .email(oauthTestEmail)
        .name("김승은oauth")
        .school(testSchool)
        .provider("카카오")
        .providerId("카카오id")
        .build();

    schoolRepository.save(testSchool);
    userRepository.save(user1);
    userRepository.save(user2);

    User localResult = userRepository.findByEmail(localTestEmail).orElseThrow(() -> new NotFoundException("local user를 찾을 수 없습니다."));
    User oauthResult = userRepository.findByEmail(oauthTestEmail).orElseThrow(() -> new NotFoundException("local user를 찾을 수 없습니다."));

    assertThat(localResult).isInstanceOf(LocalUser.class);
    assertThat(oauthResult).isInstanceOf(OAuthUser.class);
  }
}