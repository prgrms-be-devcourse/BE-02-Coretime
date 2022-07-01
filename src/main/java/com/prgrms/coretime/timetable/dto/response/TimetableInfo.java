package com.prgrms.coretime.timetable.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimetableInfo {
  private Long timetableId;
  private String name;

  public TimetableInfo(Long timetableId, String name) {
    this.timetableId = timetableId;
    this.name = name;
  }
}
