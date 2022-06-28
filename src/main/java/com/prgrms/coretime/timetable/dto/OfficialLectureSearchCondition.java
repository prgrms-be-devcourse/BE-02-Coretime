package com.prgrms.coretime.timetable.dto;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.lecture.Grade;
import com.prgrms.coretime.timetable.domain.lecture.LectureType;
import com.prgrms.coretime.timetable.dto.request.SearchType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OfficialLectureSearchCondition {
  private Integer openYear;
  private Semester semester;
  private SearchType searchType;
  private String searchWord;
  private List<Grade> grades;
  private List<LectureType> lectureTypes;
  private List<Double> credits;

  @Builder
  public OfficialLectureSearchCondition(Integer openYear,
      Semester semester, SearchType searchType, String searchWord,
      List<Grade> grades,
      List<LectureType> lectureTypes, List<Double> credits) {
    this.openYear = openYear;
    this.semester = semester;
    this.searchType = searchType;
    this.searchWord = searchWord;
    this.grades = grades;
    this.lectureTypes = lectureTypes;
    this.credits = credits;
  }
}
