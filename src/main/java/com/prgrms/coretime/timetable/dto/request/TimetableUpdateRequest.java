package com.prgrms.coretime.timetable.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimetableUpdateRequest {
  private String name;

  public TimetableUpdateRequest(String name) {
    this.name = name;
  }
}
