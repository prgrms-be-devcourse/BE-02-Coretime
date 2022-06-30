package com.prgrms.coretime.timetable.domain.repository.enrollment;

import static com.prgrms.coretime.timetable.domain.Semester.FIRST;
import static com.prgrms.coretime.timetable.domain.Semester.SECOND;
import static com.prgrms.coretime.timetable.domain.lecture.Grade.FRESHMAN;
import static com.prgrms.coretime.timetable.domain.lecture.LectureType.MAJOR;
import static com.prgrms.coretime.timetable.domain.repository.enrollment.LectureType.CUSTOM;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.timetable.domain.enrollment.Enrollment;
import com.prgrms.coretime.timetable.domain.enrollment.EnrollmentId;
import com.prgrms.coretime.timetable.domain.lecture.CustomLecture;
import com.prgrms.coretime.timetable.domain.lecture.Lecture;
import com.prgrms.coretime.timetable.domain.lecture.OfficialLecture;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
class EnrollmentRepositoryTest {
  @TestConfiguration
  static class TestConfig {
    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
      return new JPAQueryFactory(em);
    }
  }

  @Autowired
  private EnrollmentRepository enrollmentRepository;

  @Autowired
  private EntityManager em;

  private Timetable timetable;

  @BeforeEach
  void setUp() {
    School schoolA = new School("a", "bc");
    em.persist(schoolA);

    timetable = Timetable.builder()
        .name("시간표")
        .year(2022)
        .semester(FIRST)
        .build();
    em.persist(timetable);

    OfficialLecture officialLecture = OfficialLecture.builder()
        .name("강의A")
        .semester(SECOND)
        .openYear(2022)
        .grade(FRESHMAN)
        .credit(4.0)
        .code("0012")
        .lectureType(MAJOR)
        .build();
    officialLecture.setSchool(schoolA);
    em.persist(officialLecture);

    CustomLecture customLecture = CustomLecture.builder()
        .name("강의B")
        .build();
    em.persist(customLecture);

    Enrollment officialEnrollment = new Enrollment(new EnrollmentId(officialLecture.getId(), timetable.getId()));
    officialEnrollment.setLecture(officialLecture);
    officialEnrollment.setTimeTable(timetable);

    Enrollment customEnrollment = new Enrollment(new EnrollmentId(customLecture.getId(), timetable.getId()));
    customEnrollment.setLecture(customLecture);
    customEnrollment.setTimeTable(timetable);

    em.persist(officialEnrollment);
    em.persist(customEnrollment);

    em.flush();
    em.clear();
  }


  @Test
  @DisplayName("custom 강의 enrollment만 반환하는지 테스트")
  void testGetEnrollmentWithLectureById() {
    List<Enrollment> enrollments = enrollmentRepository.getEnrollmentsWithLectureByTimetableId(timetable.getId(), CUSTOM);

    for (Enrollment enrollment : enrollments) {
      Lecture lecture = enrollment.getLecture();
      assertThat(lecture.getDType()).isEqualTo("CUSTOM");
    }
  }
}