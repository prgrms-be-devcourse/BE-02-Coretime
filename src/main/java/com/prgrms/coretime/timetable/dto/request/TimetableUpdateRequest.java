package com.prgrms.coretime.timetable.dto.request;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimetableUpdateRequest {
  @NotNull
  private String name;

  @NotNull
  private Boolean isDefault;

  public TimetableUpdateRequest(String name, Boolean isDefault) {
    this.name = name;
    this.isDefault = isDefault;
  }
}
