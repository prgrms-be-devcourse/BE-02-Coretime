package com.prgrms.coretime.timetable.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.prgrms.coretime.timetable.domain.lectureDetail.Day;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LectureDetailInfo {
  private Day day;
  private LocalTime startTime;
  private LocalTime endTime;

  @Builder
  public LectureDetailInfo(Day day, LocalTime startTime, LocalTime endTime) {
    this.day = day;
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
