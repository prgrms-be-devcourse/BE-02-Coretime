package com.prgrms.coretime.timetable.dto.request;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimetableUpdateRequest {
  @NotNull
  private String name;

  public TimetableUpdateRequest(String name) {
    this.name = name;
  }
}
