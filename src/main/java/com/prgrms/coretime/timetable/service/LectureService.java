package com.prgrms.coretime.timetable.service;

import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.StringUtils.hasText;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.lecture.Grade;
import com.prgrms.coretime.timetable.domain.lecture.LectureType;
import com.prgrms.coretime.timetable.domain.lecture.OfficialLecture;
import com.prgrms.coretime.timetable.domain.repository.lecture.LectureRepository;
import com.prgrms.coretime.timetable.dto.OfficialLectureSearchCondition;
import com.prgrms.coretime.timetable.dto.request.OfficialLectureSearchRequest;
import com.prgrms.coretime.timetable.dto.request.SearchType;
import com.prgrms.coretime.timetable.dto.response.LectureDetailInfo;
import com.prgrms.coretime.timetable.dto.response.OfficialLectureInfo;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LectureService {
  private final Set<Double> allowedCreditValues = Set.of(0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0);

  private final LectureRepository lectureRepository;

  @Transactional(readOnly = true)
  public Page<OfficialLectureInfo> getOfficialLectures(Long schoolId, OfficialLectureSearchRequest officialLectureSearchRequest, Pageable pageable) {
    Page<OfficialLecture> officialLecturesPagingResult = lectureRepository.getOfficialLectures(
        createOfficialLectureSearchCondition(schoolId, officialLectureSearchRequest),
        pageable
    );

     return officialLecturesPagingResult.map(officialLecture -> {
      List<LectureDetailInfo> lectureDetails = officialLecture.getLectureDetails().stream()
          .map(lectureDetail ->
              LectureDetailInfo.builder()
                  .day(lectureDetail.getDay())
                  .startTime(lectureDetail.getStartTime())
                  .endTime(lectureDetail.getEndTime())
                  .build()
          )
          .collect(Collectors.toList());

      return OfficialLectureInfo.builder()
          .lectureId(officialLecture.getId())
          .name(officialLecture.getName())
          .professor(officialLecture.getProfessor())
          .classroom(officialLecture.getClassroom())
          .grade(officialLecture.getGrade())
          .code(officialLecture.getCode())
          .credit(officialLecture.getCredit())
          .lectureType(officialLecture.getLectureType())
          .lectureDetails(lectureDetails)
          .build();
    });
  }


  private OfficialLectureSearchCondition createOfficialLectureSearchCondition(Long schoolId, OfficialLectureSearchRequest officialLectureSearchRequest) {
    // TODO : time 조건 필요
    validateSchoolId(schoolId);
    validateYear(officialLectureSearchRequest.getYear());
    validateSemster(officialLectureSearchRequest.getSemester());
    validateGrades(officialLectureSearchRequest.getGrades());
    validateLectureTypes(officialLectureSearchRequest.getLectureTypes());
    validateCredits(officialLectureSearchRequest.getCredits());
    validateSearch(officialLectureSearchRequest.getSearchType(), officialLectureSearchRequest.getSearchWord());

    return OfficialLectureSearchCondition.builder()
        .schoolId(schoolId)
        .openYear(officialLectureSearchRequest.getYear())
        .semester(officialLectureSearchRequest.getSemester())
        .searchType(officialLectureSearchRequest.getSearchType())
        .searchWord(officialLectureSearchRequest.getSearchWord())
        .grades(officialLectureSearchRequest.getGrades())
        .lectureTypes(officialLectureSearchRequest.getLectureTypes())
        .credits(officialLectureSearchRequest.getCredits())
        .build();
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

  private void validateSemster(Semester semester) {
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
