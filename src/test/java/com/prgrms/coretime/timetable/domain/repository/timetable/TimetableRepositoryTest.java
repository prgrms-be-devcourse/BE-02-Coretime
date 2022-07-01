package com.prgrms.coretime.timetable.domain.repository.timetable;

import static com.prgrms.coretime.timetable.domain.Semester.FIRST;
import static com.prgrms.coretime.timetable.domain.Semester.SECOND;
import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import com.prgrms.coretime.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
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

  private User userA, userB;
  private List<Timetable> timetables = new ArrayList<>();

  @BeforeEach
  void setUp() {
    userA = new User("a@school.com", "testerA");
    em.persist(userA);

    userB = new User("b@school.com", "testerB");
    em.persist(userB);

    for(int i = 0; i < 10; i++) {
      Timetable timetable = Timetable.builder()
          .name("시간표 "+i)
          .year(2022)
          .semester(i <= 4 ? FIRST : SECOND)
          .user(i % 2 == 0 ? userA : userB)
          .isDefault(false)
          .build();
      em.persist(timetable);
      timetables.add(timetable);
    }
  }

  @Test
  @DisplayName("사용자가 가지고 있는 시간표에서 특정 연도와 학기 내에 중복된 시간표 이름을 확인할 수 있는지 테스트한다.")
  void testDuplicateNameCount() {
    assertThat(timetableRepository.isDuplicateTimetableName(userA.getId(), "시간표 0", 2022, FIRST)).isTrue();
    assertThat(timetableRepository.isDuplicateTimetableName(userB.getId(), "시간표 0", 2022, FIRST)).isFalse();
  }

  @Test
  @DisplayName("특정 사용자에게 속하며 특정 연도와 학기에 해당하는 시간표들을 제대로 반환하는지 테스트한다.")
  void testGetTimesTables() {
    List<Timetable> timetables = timetableRepository.getTimetables(userB.getId(), 2022, SECOND);
    assertThat(timetables.size()).isEqualTo(3);
  }

  @Test
  @DisplayName("사용자에게 속해 있는 특정 ID의 시간표를 제대로 반환하는지 테스트한다.")
  void testGetTimetableByUserIdAndTimetableIdCondition() {
    assertThat(timetableRepository.getTimetableByUserIdAndTimetableId(userA.getId(), timetables.get(1).getId())).isEmpty();
    assertThat(timetableRepository.getTimetableByUserIdAndTimetableId(userA.getId(), timetables.get(2).getId())).isNotEmpty();
  }
}