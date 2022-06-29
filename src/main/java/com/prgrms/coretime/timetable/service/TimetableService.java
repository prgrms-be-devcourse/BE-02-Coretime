
package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.common.ErrorCode.NOT_FOUND;

import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.lecture.Lecture;
import com.prgrms.coretime.timetable.domain.repository.enrollment.EnrollmentRepository;
import com.prgrms.coretime.timetable.domain.repository.timetable.TimetableRepository;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import com.prgrms.coretime.timetable.dto.request.TimetableCreateRequest;
import com.prgrms.coretime.timetable.dto.request.TimetableUpdateRequest;
import com.prgrms.coretime.timetable.dto.response.LectureDetailInfo;
import com.prgrms.coretime.timetable.dto.response.LectureInfo;
import com.prgrms.coretime.timetable.dto.response.TimetableInfo;
import com.prgrms.coretime.timetable.dto.response.TimetableResponse;
import com.prgrms.coretime.timetable.dto.response.TimetablesResponse;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class TimetableService {
  private final TimetableRepository timetableRepository;
  private final EnrollmentRepository enrollmentRepository;
  private final UserRepository userRepository;

  @Transactional
  public Long createTimetable(@RequestBody @Valid TimetableCreateRequest timetableCreateRequest) {
    // TODO : 사용자 ID 가져오는 로직이 추가적으로 필요하다.

    Long userId = 1L;
    User user  = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(NOT_FOUND));

    if(timetableRepository.isDuplicateTimetableName(userId, timetableCreateRequest.getName().trim(), timetableCreateRequest.getYear(), timetableCreateRequest.getSemester())) {
      throw new IllegalArgumentException("이미 사용중인 이름입니다.");
    }

    Timetable newTimetable = Timetable.builder()
        .name(timetableCreateRequest.getName().trim())
        .year(timetableCreateRequest.getYear())
        .semester(timetableCreateRequest.getSemester())
        .build();
    newTimetable.setUser(user);

    Timetable createdTimetable = timetableRepository.save(newTimetable);
    return createdTimetable.getId();
  }

  @Transactional(readOnly = true)
  public TimetablesResponse getTimetables(Integer year, Semester semester) {
    // TODO : 사용자 ID 가져오는 로직이 추가적으로 필요하다.

    Long userId = 1L;
    List<TimetableInfo> timetables = timetableRepository.getTimetables(userId, year, semester).stream()
        .map(timetable -> new TimetableInfo(timetable.getId(), timetable.getName()))
        .collect(Collectors.toList());

    return new TimetablesResponse(timetables);
  }

  @Transactional(readOnly = true)
  public TimetableResponse getTimetable(Long timetableId) {
    // TODO : 사용자 ID 가져오는 로직이 추가적으로 필요하다.

    Long userId = 1L;
    Timetable timetable = timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId).orElseThrow(() -> new NotFoundException(NOT_FOUND));

    List<LectureInfo> lectures = enrollmentRepository.getEnrollmentByIdWithLecture(timetableId).stream()
        .map(enrollment -> {
          Lecture lecture = enrollment.getLecture();

          List<LectureDetailInfo> lectureDetails = lecture.getLectureDetails().stream()
              .map(lectureDetail ->
                  LectureDetailInfo.builder()
                      .day(lectureDetail.getDay())
                      .startTime(lectureDetail.getStartTime())
                      .endTime(lectureDetail.getEndTime())
                      .build()
              )
              .collect(Collectors.toList());

          return LectureInfo.builder()
              .lectureId(lecture.getId())
              .name(lecture.getName())
              .professor(lecture.getProfessor())
              .classroom(lecture.getClassroom())
              .lectureDetails(lectureDetails)
              .build();
        })
        .collect(Collectors.toList());

    return TimetableResponse.builder()
        .timetableId(timetable.getId())
        .name(timetable.getName())
        .year(timetable.getYear())
        .semester(timetable.getSemester())
        .lectures(lectures)
        .build();
  }

  @Transactional
  public void updateTimetableName(Long timetableId, TimetableUpdateRequest timetableUpdateRequest) {
    // TODO : 시간표가 사용자의 것인지 확인해야한다.

    Timetable timetable = timetableRepository.findById(timetableId).orElseThrow(() -> new NotFoundException(NOT_FOUND));

    timetable.updateName(timetableUpdateRequest.getName().trim());
  }

  @Transactional
  public void deleteTimetable(Long timetableId) {
    // TODO : 시간표가 사용자의 것인지 확인해야한다.

    Timetable timetable = timetableRepository.findById(timetableId).orElseThrow(() -> new NotFoundException(NOT_FOUND));

    // TODO : enrollment에서 삭제(timetableId에 해당하는 항목 삭제)
    // TODO : CUSTOM 강의 삭제

    timetableRepository.delete(timetable);
  }
}
