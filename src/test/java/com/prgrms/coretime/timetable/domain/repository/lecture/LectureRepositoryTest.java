package com.prgrms.coretime.timetable.domain.repository.lecture;

import static com.prgrms.coretime.timetable.domain.Semester.SECOND;
import static com.prgrms.coretime.timetable.domain.lecture.Grade.FRESHMAN;
import static com.prgrms.coretime.timetable.domain.lecture.LectureType.MAJOR;
import static com.prgrms.coretime.timetable.domain.lectureDetail.Day.MON;
import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.timetable.domain.lecture.CustomLecture;
import com.prgrms.coretime.timetable.domain.lecture.Lecture;
import com.prgrms.coretime.timetable.domain.lecture.OfficialLecture;
import com.prgrms.coretime.timetable.domain.lectureDetail.LectureDetail;
import com.prgrms.coretime.timetable.domain.repository.lectureDetail.LectureDetailRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalTime;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@DataJpaTest
class LectureRepositoryTest {
  @TestConfiguration
  static class TestConfig {
    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
      return new JPAQueryFactory(em);
    }
  }

  @Autowired
  LectureRepository lectureRepository;

  @Autowired
  LectureDetailRepository lectureDetailRepository;

  @Autowired
  EntityManager em;

  School schoolA;
  OfficialLecture officialLectureA;
  CustomLecture customLecture;
  LectureDetail lectureDetailA;

  @BeforeEach
  void setUp() {
    schoolA = new School("a", "bc");
    em.persist(schoolA);

    officialLectureA = OfficialLecture.builder()
        .name("강의A")
        .professor("교수A")
        .classroom("A123")
        .semester(SECOND)
        .openYear(2022)
        .grade(FRESHMAN)
        .credit(4.0)
        .code("0012")
        .lectureType(MAJOR)
        .build();
    officialLectureA.setSchool(schoolA);
    em.persist(officialLectureA);

    lectureDetailA = LectureDetail.builder()
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(10, 50))
        .day(MON)
        .build();
    em.persist(lectureDetailA);
    lectureDetailA.setLecture(officialLectureA);

    customLecture = CustomLecture.builder()
        .name("aaa")
        .professor("bbb")
        .classroom("ccc")
        .build();
    em.persist(customLecture);

    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("findOfficialLectureById()를 통해 정상적으로 엔티티를 받아오는지 확인하는 테스트")
  void testFindOfficialLectureById() {
    Optional<OfficialLecture> notEmptyOfficialLecture = lectureRepository.getOfficialLectureById(officialLectureA.getId());
    assertThat(notEmptyOfficialLecture).isNotEmpty();
  }

  @Test
  @DisplayName("엔티티에 DTYPE을 포함하고 있는지 테스트")
  void testEntityIncludeDType() throws Exception {
    Lecture officialLecture = lectureRepository.findById(officialLectureA.getId()).orElseThrow(() -> new Exception());
    assertThat(officialLecture.getDType()).isEqualTo("OFFICIAL");
  }

  @Test
  @DisplayName("isCustomLecture() 메서드가 customLecture 여부를 잘 판단할 수 있는지 테스트")
  void testIsCustomLecture() {
    assertThat(lectureRepository.isCustomLecture(officialLectureA.getId())).isFalse();
    assertThat(lectureRepository.isCustomLecture(customLecture.getId())).isTrue();
  }
}