package com.prgrms.coretime.timetable.domain.repository;

import static com.prgrms.coretime.timetable.domain.Semester.FIRST;
import static com.prgrms.coretime.timetable.domain.Semester.SECOND;
import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
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
  TimetableRepository timetableRepository;

  @Test
  @DisplayName("중복되는 특정 연도와 학기에서 중복되는 이름 잘 검증하는지 테스트")
  void testDuplicateNameCount() {
    for(int i = 0; i < 10; i++) {
      Timetable timetable = Timetable.builder()
          .name("시간표"+i)
          .year(2022)
          .semester(i % 2 == 0 ? FIRST : SECOND)
          .build();
      timetableRepository.save(timetable);
    }

    long count = timetableRepository.countDuplicateTimetableName("시간표2", 2022, FIRST);
    assertThat(count).isEqualTo(1);

    count = timetableRepository.countDuplicateTimetableName("시간표2", 2022, SECOND);
    assertThat(count).isEqualTo(0);
  }



}