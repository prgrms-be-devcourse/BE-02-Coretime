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
  private LectureType lectureType;
  private List<Double> credits;

  // TODO : time은 가장 마지막에 구현

  @Builder
  private OfficialLectureSearchCondition(Integer openYear, Semester semester, List<Grade> grades, LectureType lectureType, List<Double> credits) {
    this.openYear = openYear;
    this.semester = semester;
    this.grades = grades;
    this.lectureType = lectureType;
    this.credits = credits;
  }

  public static OfficialLectureSearchCondition of(OfficialLectureSearchRequest officialLectureSearchRequest) {
    log.info("{}", officialLectureSearchRequest.getYear());

    return OfficialLectureSearchCondition.builder()
        .openYear(officialLectureSearchRequest.getYear())
        .semester(officialLectureSearchRequest.getSemester())
        .build();
  }
}
