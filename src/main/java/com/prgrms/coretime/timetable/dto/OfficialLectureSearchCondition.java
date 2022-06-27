package com.prgrms.coretime.timetable.dto;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.lecture.Grade;
import com.prgrms.coretime.timetable.domain.lecture.LectureType;
import com.prgrms.coretime.timetable.dto.request.OfficialLectureSearchRequest;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class OfficialLectureSearchCondition {
  private Integer openYear;
  private Semester semester;
  private List<Grade> grades;
  private List<LectureType> lectureTypes;
  private List<Double> credits;

  // TODO : 학교에 대한 필터링
  // TODO : time은 가장 마지막에 구현

  @Builder
  private OfficialLectureSearchCondition(Integer openYear, Semester semester, List<Grade> grades, List<LectureType> lectureTypes, List<Double> credits) {
    this.openYear = openYear;
    this.semester = semester;
    this.grades = grades;
    this.lectureTypes = lectureTypes;
    this.credits = credits;
  }

  public static OfficialLectureSearchCondition of(OfficialLectureSearchRequest officialLectureSearchRequest) {
    return OfficialLectureSearchCondition.builder()
        .openYear(officialLectureSearchRequest.getYear())
        .semester(officialLectureSearchRequest.getSemester())
        .grades(officialLectureSearchRequest.getGrades())
        .lectureTypes(officialLectureSearchRequest.getLectureTypes())
        .build();
  }
}
