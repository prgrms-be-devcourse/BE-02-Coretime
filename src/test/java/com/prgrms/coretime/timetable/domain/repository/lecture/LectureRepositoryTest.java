package com.prgrms.coretime.timetable.domain.repository.lecture;

import static com.prgrms.coretime.timetable.domain.Semester.SECOND;
import static com.prgrms.coretime.timetable.domain.lecture.Grade.FRESHMAN;
import static com.prgrms.coretime.timetable.domain.lecture.Grade.JUNIOR;
import static com.prgrms.coretime.timetable.domain.lecture.LectureType.ELECTIVE;
import static com.prgrms.coretime.timetable.domain.lecture.LectureType.MAJOR;
import static com.prgrms.coretime.timetable.domain.lectureDetail.Day.FRI;
import static com.prgrms.coretime.timetable.domain.lectureDetail.Day.MON;
import static com.prgrms.coretime.timetable.domain.lectureDetail.Day.WED;
import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.coretime.timetable.domain.lecture.OfficialLecture;
import com.prgrms.coretime.timetable.domain.lectureDetail.LectureDetail;
import com.prgrms.coretime.timetable.domain.repository.LectureDetailRepository;
import com.prgrms.coretime.timetable.dto.OfficialLectureSearchCondition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalTime;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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


  @BeforeEach
  void setUp() {
    OfficialLecture officialLectureA = OfficialLecture.builder()
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
    lectureRepository.save(officialLectureA);

    LectureDetail lectureDetailA = LectureDetail.builder()
        .day(MON)
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(10, 50))
        .build();
    lectureDetailA.addLecture(officialLectureA);
    lectureDetailRepository.save(lectureDetailA);

    OfficialLecture officialLectureB = OfficialLecture.builder()
        .name("강의B")
        .professor("교수B")
        .classroom("B123")
        .semester(SECOND)
        .openYear(2022)
        .grade(FRESHMAN)
        .credit(1.0)
        .code("0013")
        .lectureType(ELECTIVE)
        .build();
    lectureRepository.save(officialLectureB);

    LectureDetail lectureDetailB = LectureDetail.builder()
        .day(WED)
        .startTime(LocalTime.of(15, 0))
        .endTime(LocalTime.of(16, 50))
        .build();
    lectureDetailB.addLecture(officialLectureB);
    lectureDetailRepository.save(lectureDetailB);

    OfficialLecture officialLectureC = OfficialLecture.builder()
        .name("강의C")
        .professor("교수C")
        .classroom("C123")
        .semester(SECOND)
        .openYear(2022)
        .grade(JUNIOR)
        .credit(3.0)
        .code("0014")
        .lectureType(MAJOR)
        .build();
    lectureRepository.save(officialLectureC);

    LectureDetail lectureDetailC = LectureDetail.builder()
        .day(FRI)
        .startTime(LocalTime.of(15, 0))
        .endTime(LocalTime.of(16, 50))
        .build();
    lectureDetailC.addLecture(officialLectureC);
    lectureDetailRepository.save(lectureDetailC);
  }

  @Test
  @DisplayName("조건 없는 강의 조회 테스트")
  void testWithoutCondition() {
    OfficialLectureSearchCondition condition = OfficialLectureSearchCondition.builder().build();
    PageRequest pageRequest = PageRequest.of(0, 20);

    Page<OfficialLecture> lectures = lectureRepository.findOfficialLectures(condition, pageRequest);
    assertThat(lectures.getTotalElements()).isEqualTo(3);
  }
}