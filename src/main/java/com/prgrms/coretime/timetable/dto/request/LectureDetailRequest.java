package com.prgrms.coretime.timetable.dto.request;

import com.prgrms.coretime.timetable.domain.lectureDetail.Day;
import com.prgrms.coretime.timetable.util.TimeFormatConstraint;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LectureDetailRequest {
  @NotNull
  private Day day;

  @ApiModelProperty(example = "09:00")
  @TimeFormatConstraint
  private String startTime;

  @ApiModelProperty(example = "09:50")
  @TimeFormatConstraint
  private String endTime;

  @Builder
  public LectureDetailRequest(Day day, String startTime, String endTime) {
    this.day = day;
    this.startTime = startTime;
    this.endTime = endTime;
  }
}