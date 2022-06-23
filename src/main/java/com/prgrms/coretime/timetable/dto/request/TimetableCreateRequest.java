package com.prgrms.coretime.timetable.dto.request;

import com.prgrms.coretime.timetable.domain.Semester;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TimetableCreateRequest {
  @NotNull
  private String name;

  @NotNull
  private Semester semester;

  @NotNull
  private Integer year;

  @Builder
  public TimetableCreateRequest(String name, Semester semester, Integer year) {
    this.name = name;
    this.semester = semester;
    this.year = year;
  }
}
