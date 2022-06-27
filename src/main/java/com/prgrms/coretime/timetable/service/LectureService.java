package com.prgrms.coretime.timetable.service;

import com.prgrms.coretime.timetable.domain.lecture.OfficialLecture;
import com.prgrms.coretime.timetable.domain.repository.lecture.LectureRepository;
import com.prgrms.coretime.timetable.dto.response.LectureDetailInfo;
import com.prgrms.coretime.timetable.dto.response.OfficialLectureInfo;
import com.prgrms.coretime.timetable.dto.response.OfficialLecturesResponse;
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

  @Transactional(readOnly = true)
  public OfficialLecturesResponse getOfficialLectures(Pageable pageable) {
    Page<OfficialLecture> officialLecturesPagingResult = lectureRepository.findOfficialLectures(null, pageable);

    List<OfficialLectureInfo> content = officialLecturesPagingResult.getContent().stream().map(officialLecture -> {
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
           .credit(officialLecture.getCredit())
           .lectureType(officialLecture.getLectureType())
           .lectureDetails(lectureDetails)
           .build();
     })
        .collect(Collectors.toList());


    return new OfficialLecturesResponse(content, officialLecturesPagingResult.getPageable());
  }
}
