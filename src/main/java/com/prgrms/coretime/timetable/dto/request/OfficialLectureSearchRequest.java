package com.prgrms.coretime.timetable.dto.request;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.lecture.Grade;
import com.prgrms.coretime.timetable.domain.lecture.LectureType;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OfficialLectureSearchRequest {
  @NotNull
  private Integer year;

  @NotNull
  private Semester semester;

  @NotEmpty
  private List<LectureType> lectureTypes;

  @NotEmpty
  private List<Grade> grades;

  @NotEmpty
  private List<Double> credits;

  // TODO : 학교에 대한 필터링
  // TODO : time은 가장 마지막에 구현

  @Builder
  public OfficialLectureSearchRequest(Integer year,
      Semester semester,
      List<LectureType> lectureTypes,
      List<Grade> grades, List<Double> credits) {
    this.year = year;
    this.semester = semester;
    this.lectureTypes = lectureTypes;
    this.grades = grades;
    this.credits = credits;
  }
}
