package com.prgrms.coretime.timetable.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimetableInfo {
  private Long timetableId;
  private String name;
  private Boolean isDefault;

  public TimetableInfo(Long timetableId, String name, Boolean isDefault) {
    this.timetableId = timetableId;
    this.name = name;
    this.isDefault = isDefault;
  }
}
