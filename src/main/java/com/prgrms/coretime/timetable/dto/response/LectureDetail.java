package com.prgrms.coretime.timetable.dto.response;

import com.prgrms.coretime.timetable.domain.lectureDetail.Day;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LectureDetail {
  private Day day;
  private LocalTime startTime;
  private LocalTime endTime;

  @Builder
  public LectureDetail(Day day, LocalTime startTime, LocalTime endTime) {
    this.day = day;
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
