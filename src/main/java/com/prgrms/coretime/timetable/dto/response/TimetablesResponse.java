package com.prgrms.coretime.timetable.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimetablesResponse {
  private List<TimetableInfo> timetables;

  public TimetablesResponse(List<TimetableInfo> timetables) {
    this.timetables = timetables;
  }
}
