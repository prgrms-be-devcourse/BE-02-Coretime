package com.prgrms.coretime.timetable.dto.request;

import com.prgrms.coretime.timetable.domain.Semester;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimetableCreateRequest {
  @NotNull
  private String name;

  @NotNull
  private Integer year;

  @NotNull
  private Semester semester;

  @Builder
  public TimetableCreateRequest(String name,  Integer year, Semester semester) {
    this.name = name;
    this.year = year;
    this.semester = semester;
  }
}
