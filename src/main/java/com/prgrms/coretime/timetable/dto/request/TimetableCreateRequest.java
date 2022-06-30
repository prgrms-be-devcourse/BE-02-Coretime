package com.prgrms.coretime.timetable.dto.request;

import com.prgrms.coretime.timetable.domain.Semester;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimetableCreateRequest {
  @ApiModelProperty(example = "시간표 이름")
  @NotNull
  private String name;

  @ApiModelProperty(example = "2022")
  @NotNull
  private Integer year;

  @ApiModelProperty(example = "SECOND")
  @NotNull
  private Semester semester;

  @Builder
  public TimetableCreateRequest(String name,  Integer year, Semester semester) {
    this.name = name;
    this.year = year;
    this.semester = semester;
  }
}
