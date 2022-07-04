package com.prgrms.coretime.timetable.domain.lectureDetail;

import static com.prgrms.coretime.timetable.domain.Day.MON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.prgrms.coretime.timetable.domain.Day;
import com.prgrms.coretime.timetable.domain.LectureDetail;
import java.time.LocalTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LectureDetailTest {
  @ParameterizedTest
  @MethodSource("lectureDetailValidationParameter")
  @DisplayName("LectureDetail의 필드 validation 테스트")
  void testLectureDetailValidation(LocalTime startTime, LocalTime endTime, Day day, String errorMessage) {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      LectureDetail.builder()
          .startTime(startTime)
          .endTime(endTime)
          .day(day)
          .build();
    });

    assertThat(exception.getMessage()).isEqualTo(errorMessage);
  }

  private static Stream<Arguments> lectureDetailValidationParameter() {
    return Stream.of(
        Arguments.of(null, LocalTime.of(11, 50), MON, "startTime은 null일 수 없습니다."),
        Arguments.of(LocalTime.of(11, 0), null, MON, "endTime은 null일 수 없습니다."),
        Arguments.of(LocalTime.of(11, 0, 1), LocalTime.of(11, 50), MON, "잘못된 시간 포맷입니다."),
        Arguments.of(LocalTime.of(11, 0), LocalTime.of(11, 50, 1), MON, "잘못된 시간 포맷입니다."),
        Arguments.of(LocalTime.of(11, 0), LocalTime.of(11, 50), null, "day는 null일 수 없습니다.")
    );
  }
}