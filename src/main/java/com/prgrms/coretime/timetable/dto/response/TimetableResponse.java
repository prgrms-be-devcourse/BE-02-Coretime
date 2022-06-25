package com.prgrms.coretime.timetable.dto.response;

import com.prgrms.coretime.timetable.domain.Semester;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimetableResponse {
  private Long timetableId;
  private String name;
  private int year;
  private Semester semester;
  private List<LectureDetail> lectureDetails;

  @Builder
  public TimetableResponse(Long timetableId, String name, int year, Semester semester, List<LectureDetail> lectureDetails) {
    this.timetableId = timetableId;
    this.name = name;
    this.year = year;
    this.semester = semester;
    this.lectureDetails = lectureDetails;
  }
}
