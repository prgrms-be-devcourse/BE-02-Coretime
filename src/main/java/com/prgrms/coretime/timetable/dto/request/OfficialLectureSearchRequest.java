package com.prgrms.coretime.timetable.dto.request;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.lecture.LectureType;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OfficialLectureSearchRequest {
  @NotNull
  private Integer year;

  @NotNull
  private Semester semester;

  private List<LectureType> lectureTypes;

  public OfficialLectureSearchRequest(Integer year,
      Semester semester,
      List<LectureType> lectureTypes) {
    this.year = year;
    this.semester = semester;
    this.lectureTypes = lectureTypes;
  }
}
