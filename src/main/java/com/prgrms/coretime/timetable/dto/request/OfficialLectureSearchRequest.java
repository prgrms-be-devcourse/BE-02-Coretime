package com.prgrms.coretime.timetable.dto.request;

import com.prgrms.coretime.timetable.domain.Semester;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OfficialLectureSearchRequest {
  @NotNull
  private Integer year;

  @NotNull
  private Semester semester;

  public OfficialLectureSearchRequest(Integer year, Semester semester) {
    this.year = year;
    this.semester = semester;
  }
}
