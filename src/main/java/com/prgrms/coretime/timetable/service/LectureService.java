package com.prgrms.coretime.timetable.service;

import com.prgrms.coretime.timetable.domain.OfficialLecture;
import com.prgrms.coretime.timetable.domain.repository.lecture.LectureRepository;
import com.prgrms.coretime.timetable.dto.OfficialLectureSearchCondition;
import com.prgrms.coretime.timetable.dto.request.OfficialLectureSearchRequest;
import com.prgrms.coretime.timetable.dto.response.LectureDetailInfo;
import com.prgrms.coretime.timetable.dto.response.OfficialLectureInfo;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LectureService {
  private final LectureRepository lectureRepository;
  private final LectureValidator lectureValidator;

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
    lectureValidator.validateOfficialLectureSearchRequest(schoolId, officialLectureSearchRequest);

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
}
