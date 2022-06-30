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

  private SearchType searchType;

  private String searchWord;

  @NotEmpty
  private List<Grade> grades;

  @NotEmpty
  private List<LectureType> lectureTypes;

  @NotEmpty
  private List<Double> credits;

  @Builder
  public OfficialLectureSearchRequest(Integer year,
      Semester semester, SearchType searchType, String searchWord,
      List<Grade> grades,
      List<LectureType> lectureTypes, List<Double> credits) {
    this.year = year;
    this.semester = semester;
    this.searchType = searchType;
    this.searchWord = searchWord;
    this.grades = grades;
    this.lectureTypes = lectureTypes;
    this.credits = credits;
  }
}
