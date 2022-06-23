package com.prgrms.coretime.timetable.domain.timetable;

import static com.prgrms.coretime.timetable.domain.Semester.SECOND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TimetableTest {
  @Nested
  @DisplayName("Timetable 엔티티 필드의 검증을 테스트")
  class TimetableFieldValidationTest {
    @Nested
    @DisplayName("name 필드에 대한 검증 테스트")
    class NameValidationTest {
      @Test
      @DisplayName("name 필드 길이 제한 10 검증에 대한 테스트")
      void testNameLengthValidation() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
          Timetable.builder()
              .name("홍유석의 2022년 2학기 이상적인 시간표")
              .semester(SECOND)
              .year(2022)
              .build()
        );

        assertThat(exception.getMessage()).isEqualTo("1 <= name <= 10");
      }

      @Test
      @DisplayName("name 필드 ' ' 검증에 대한 테스트")
      void testNameBlankValidation() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            Timetable.builder()
                .name(" ")
                .semester(SECOND)
                .year(2022)
                .build()
        );

        assertThat(exception.getMessage()).isEqualTo("name cannot be null blank");
      }

      @Test
      @DisplayName("name 필드 null 여부 검증에 대한 테스트")
      void testNameNullValidation() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            Timetable.builder()
                .name(null)
                .semester(SECOND)
                .year(2022)
                .build()
        );

        assertThat(exception.getMessage()).isEqualTo("name cannot be null blank");
      }
    }

    @Nested
    @DisplayName("semester 필드에 대한 검증 테스트")
    class SemesterValidationTest{
      @Test
      @DisplayName("semester 필드 null 여부 검증에 대한 테스트")
      void testSemesterNullValidation() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            Timetable.builder()
                .name("기본 시간표")
                .semester(null)
                .year(2022)
                .build()
        );

        assertThat(exception.getMessage()).isEqualTo("semester cannot be null");
      }
    }

    @Nested
    @DisplayName("year 필드에 대한 검증 테스트")
    class YearValidationTest {
      @Test
      @DisplayName("year 필드 null 여부 검증에 대한 테스트")
      void testYearNullValidation() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            Timetable.builder()
                .name("기본 시간표")
                .semester(SECOND)
                .year(null)
                .build()
        );

        assertThat(exception.getMessage()).isEqualTo("year cannot be null");
      }
    }
  }
}