package com.prgrms.coretime.timetable.service;

import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.StringUtils.hasText;

import com.prgrms.coretime.timetable.domain.Grade;
import com.prgrms.coretime.timetable.domain.LectureType;
import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.dto.request.OfficialLectureSearchRequest;
import com.prgrms.coretime.timetable.dto.request.SearchType;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class LectureValidator {
  private final Set<Double> allowedCreditValues = Set.of(0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0);

  public void validateOfficialLectureSearchRequest(Long schoolId, OfficialLectureSearchRequest officialLectureSearchRequest) {
    validateSchoolId(schoolId);
    validateYear(officialLectureSearchRequest.getYear());
    validateSemester(officialLectureSearchRequest.getSemester());
    validateGrades(officialLectureSearchRequest.getGrades());
    validateLectureTypes(officialLectureSearchRequest.getLectureTypes());
    validateCredits(officialLectureSearchRequest.getCredits());
    validateSearch(officialLectureSearchRequest.getSearchType(), officialLectureSearchRequest.getSearchWord());
  }

  private void validateSchoolId(Long schoolId) {
    notNull(schoolId, "schoolId는 null일 수 없습니다.");
  }

  private void validateYear(Integer year) {
    notNull(year, "year는 null일 수 없습니다.");
    if(year <= 0) {
      throw new IllegalArgumentException("year는 0보다 작거나 같을 수 없습니다.");
    }
  }

  private void validateSemester(Semester semester) {
    notNull(semester, "semester는 null일 수 없습니다.");
  }

  private void validateGrades(List<Grade> grades) {
    notEmpty(grades, "grades는 null일 수 없고 최소 1개의 요소를 가져야 합니다.");
    grades.forEach(grade -> notNull(grade, "grades는 null 요소를 허용하지 않습니다."));
  }

  private void validateLectureTypes(List<LectureType> lectureTypes) {
    notEmpty(lectureTypes, "lectureTypes는 null일 수 없고 최소 1개의 요소를 가져야 합니다.");
    lectureTypes.forEach(lectureType -> notNull(lectureType, "lectureTypes는 null 요소를 허용하지 않습니다."));
  }

  private void validateCredits(List<Double> credits) {
    notEmpty(credits, "credits는 null일 수 없고 최소 1개의 요소를 가져야 합니다.");

    credits.forEach(credit -> notNull(credit, "credits는 null 요소를 허용하지 않습니다."));

    if(!allowedCreditValues.containsAll(credits)) {
      throw new IllegalArgumentException("credit은 0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0 값만 허용합니다.");
    }
  }

  private void validateSearch(SearchType searchType, String searchWord) {
    if(searchType == null && hasText(searchWord)) {
      throw new IllegalArgumentException("searchType이 null일 수 없습니다.");
    }

    if(searchType != null && !hasText(searchWord)) {
      throw new IllegalArgumentException("searchWord가 null일 수 없습니다.");
    }

    if(searchType != null && hasText(searchWord) && searchWord.length() < 2) {
      throw new IllegalArgumentException("searchWord는 2글자 이상이어야 합니다.");
    }
  }
}
