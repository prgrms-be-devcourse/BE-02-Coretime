package com.prgrms.coretime.timetable.dto.request;

import com.prgrms.coretime.timetable.domain.lectureDetail.Day;
import com.prgrms.coretime.timetable.util.TimeFormatConstraint;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomLectureDetail {
  @NotNull
  private Day day;

  @TimeFormatConstraint
  private String startTime;

  @TimeFormatConstraint
  private String endTime;

  @Builder
  public CustomLectureDetail(Day day, String startTime, String endTime) {
    this.day = day;
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
