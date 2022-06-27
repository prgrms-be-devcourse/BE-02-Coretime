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
@JsonInclude(Include.NON_NULL)
public class LectureDetailInfo {
  private Long lectureDetailId;
  private Day day;
  private LocalTime startTime;
  private LocalTime endTime;

  @QueryProjection
  @Builder
  public LectureDetailInfo(Long lectureDetailId, Day day, LocalTime startTime, LocalTime endTime) {
    this.lectureDetailId = lectureDetailId;
    this.day = day;
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
