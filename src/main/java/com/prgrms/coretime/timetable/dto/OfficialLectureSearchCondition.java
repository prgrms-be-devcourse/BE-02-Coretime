package com.prgrms.coretime.timetable.dto;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.lecture.Grade;
import com.prgrms.coretime.timetable.domain.lecture.LectureType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OfficialLectureSearchCondition {
  private Integer year;
  private Semester semester;
  private List<Grade> grades;
  private LectureType lectureType;
  private List<Double> credits;

  // TODO : time은 가장 마지막에 구현

  @Builder
  public OfficialLectureSearchCondition(Integer year,
      Semester semester, List<Grade> grades,
      LectureType lectureType, List<Double> credits) {
    this.year = year;
    this.semester = semester;
    this.grades = grades;
    this.lectureType = lectureType;
    this.credits = credits;
  }
}
