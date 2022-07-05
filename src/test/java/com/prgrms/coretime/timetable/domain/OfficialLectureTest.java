package com.prgrms.coretime.timetable.domain;

import static com.prgrms.coretime.timetable.domain.Semester.SECOND;
import static com.prgrms.coretime.timetable.domain.Grade.JUNIOR;
import static com.prgrms.coretime.timetable.domain.LectureType.MAJOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.prgrms.coretime.timetable.domain.Grade;
import com.prgrms.coretime.timetable.domain.LectureType;
import com.prgrms.coretime.timetable.domain.OfficialLecture;
import com.prgrms.coretime.timetable.domain.Semester;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class OfficialLectureTest {
  @ParameterizedTest
  @MethodSource("officialLectureValidationParameter")
  @DisplayName("OfficialLecture의 필드 validation 테스트")
  void testOfficialLectureValidation(String name, String professor, String classroom, Semester semester, Integer openYear, Grade grade, Double credit, String code, LectureType lectureType, String errorMessage) {
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
        OfficialLecture.builder()
            .name(name)
            .professor(professor)
            .classroom(classroom)
            .semester(semester)
            .openYear(openYear)
            .grade(grade)
            .credit(credit)
            .code(code)
            .lectureType(lectureType)
            .build()
    );

    assertThat(exception.getMessage()).isEqualTo(errorMessage);
  }

  private static Stream<Arguments> officialLectureValidationParameter() {
    return Stream.of(
        Arguments.of(null, "교수1", "501", SECOND, 2022, JUNIOR, 3.0, "2122121", MAJOR, "name은 null이거나 빈칸일 수 없습니다."),
        Arguments.of("", "교수1", "501", SECOND, 2022, JUNIOR, 3.0, "2122121", MAJOR, "name은 null이거나 빈칸일 수 없습니다."),
        Arguments.of(" ", "교수1", "501", SECOND, 2022, JUNIOR, 3.0, "2122121", MAJOR, "name은 null이거나 빈칸일 수 없습니다."),
        Arguments.of("과목과목과목과목과목과목과목과목과목과목과목과목과목과목과목과목과목과목과목", "교수1", "501", SECOND, 2022, JUNIOR, 3.0, "2122121", MAJOR, "name의 길이는 1 ~ 30 입니다."),
        Arguments.of("과목1", "", "501", SECOND, 2022, JUNIOR, 3.0, "2122121", MAJOR, "professor는 빈칸일 수 없습니다."),
        Arguments.of("과목1", "교수교수교수교수교수교수교수교수교수교수교수", "501", SECOND, 2022, JUNIOR, 3.0, "2122121", MAJOR, "professor의 길이는 1 ~ 20 입니다."),
        Arguments.of("과목1", "교수1", "", SECOND, 2022, JUNIOR, 3.0, "2122121", MAJOR, "classroom은 빈칸일 수 없습니다."),
        Arguments.of("과목1", "교수1", "501501501501", SECOND, 2022, JUNIOR, 3.0, "2122121", MAJOR, "classroom의 길이는 1 ~ 10 입니다."),
        Arguments.of("과목1", "교수1", "501", null, 2022, JUNIOR, 3.0, "2122121", MAJOR, "semester는 null일 수 없습니다."),
        Arguments.of("과목1", "교수1", "501", SECOND, null, JUNIOR, 3.0, "2122121", MAJOR, "openYear는 null일 수 없습니다."),
        Arguments.of("과목1", "교수1", "501", SECOND, -1, JUNIOR, 3.0, "2122121", MAJOR, "openYear는 0보다 작거나 같을 수 없습니다."),
        Arguments.of("과목1", "교수1", "501", SECOND, 2022, null, 3.0, "2122121", MAJOR, "grade는 null일 수 없습니다."),
        Arguments.of("과목1", "교수1", "501", SECOND, 2022, JUNIOR, null, "2122121", MAJOR, "credit는 null일 수 없습니다."),
        Arguments.of("과목1", "교수1", "501", SECOND, 2022, JUNIOR, 3.0, null, MAJOR, "code는 null이거나 빈칸일 수 없습니다"),
        Arguments.of("과목1", "교수1", "501", SECOND, 2022, JUNIOR, 3.0, "", MAJOR, "code는 null이거나 빈칸일 수 없습니다"),
        Arguments.of("과목1", "교수1", "501", SECOND, 2022, JUNIOR, 3.0, " ", MAJOR, "code는 null이거나 빈칸일 수 없습니다"),
        Arguments.of("과목1", "교수1", "501", SECOND, 2022, JUNIOR, 3.0, "21212121212121212122122121", MAJOR, "code는 길이는 1 ~ 10 입니다."),
        Arguments.of("과목1", "교수1", "501", SECOND, 2022, JUNIOR, 3.0, "2122121", null, "lectureType은 null일 수 없습니다.")
    );
  }
}