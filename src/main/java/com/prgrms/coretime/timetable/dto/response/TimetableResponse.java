package com.prgrms.coretime.timetable.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimetableResponse {
  private List<TimetableInfo> timetables;

  public TimetableResponse(
      List<TimetableInfo> timetables) {
    this.timetables = timetables;
  }
}
