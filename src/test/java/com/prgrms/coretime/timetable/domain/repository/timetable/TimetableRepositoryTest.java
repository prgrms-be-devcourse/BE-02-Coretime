package com.prgrms.coretime.timetable.domain.repository.timetable;

import com.prgrms.coretime.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@DataJpaTest
class TimetableRepositoryTest {
  @TestConfiguration
  static class TestConfig {
    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
      return new JPAQueryFactory(em);
    }
  }

  @Autowired
  private TimetableRepository timetableRepository;

  @Autowired
  private EntityManager em;

  private User user;

  @BeforeEach
  void setUp() {
    User user = new User("a@school.com", "tester");

  }

  @Test
  @DisplayName("사용자가 가지고 있는 시간표에서 특정 연도와 학기 내에 중복된 시간표 이름이 있는지 확인한다.")
  void testDuplicateNameCount() {

  }

  @Test
  @DisplayName("시간표 목록 조회 테스트")
  void testGetTimesTables() {
  }
}