package com.prgrms.coretime.timetable.dto.response;

import com.prgrms.coretime.timetable.domain.Semester;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FriendDefaultTimetableInfo {
  private int year;
  private Semester semester;

  public FriendDefaultTimetableInfo(int year, Semester semester) {
    this.year = year;
    this.semester = semester;
  }
}
