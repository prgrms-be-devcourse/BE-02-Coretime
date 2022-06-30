
package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.common.ErrorCode.NOT_FOUND;
import static com.prgrms.coretime.timetable.domain.repository.enrollment.LectureType.ALL;
import static com.prgrms.coretime.timetable.domain.repository.enrollment.LectureType.CUSTOM;

import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.lecture.Lecture;
import com.prgrms.coretime.timetable.domain.repository.enrollment.EnrollmentRepository;
import com.prgrms.coretime.timetable.domain.repository.enrollment.LectureType;
import com.prgrms.coretime.timetable.domain.repository.lecture.LectureRepository;
import com.prgrms.coretime.timetable.domain.repository.lectureDetail.LectureDetailRepository;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimetableService {
  private final TimetableRepository timetableRepository;
  private final EnrollmentRepository enrollmentRepository;
  private final LectureDetailRepository lectureDetailRepository;
  private final LectureRepository lectureRepository;
  private final UserRepository userRepository;

  @Transactional
  public Long createTimetable(@RequestBody @Valid TimetableCreateRequest timetableCreateRequest) {
    // TODO : 사용자 ID 가져오는 로직이 필요하다.

    Long userId = 1L;
    User user  = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(NOT_FOUND));

    String timetableName = timetableCreateRequest.getName().trim();
    Integer year = timetableCreateRequest.getYear();
    Semester semester = timetableCreateRequest.getSemester();

    if(timetableRepository.isDuplicateTimetableName(userId, timetableName, year, semester)) {
      throw new IllegalArgumentException("이미 사용중인 이름입니다.");
    }

    Timetable newTimetable = Timetable.builder()
        .name(timetableName)
        .year(year)
        .semester(semester)
        .build();
    newTimetable.setUser(user);

    Timetable createdTimetable = timetableRepository.save(newTimetable);
    return createdTimetable.getId();
  }

  @Transactional(readOnly = true)
  public TimetablesResponse getTimetables(Integer year, Semester semester) {
    // TODO : 사용자 ID 가져오는 로직이 필요하다.

    Long userId = 1L;
    List<TimetableInfo> timetables = timetableRepository.getTimetables(userId, year, semester).stream()
        .map(timetable -> new TimetableInfo(timetable.getId(), timetable.getName()))
        .collect(Collectors.toList());

    return new TimetablesResponse(timetables);
  }

  @Transactional(readOnly = true)
  public TimetableResponse getTimetable(Long timetableId) {
    // TODO : 사용자 ID 가져오는 로직이 필요하다.

    Long userId = 1L;
    Timetable timetable = getTimetableOfUser(userId, timetableId);

    List<LectureInfo> lectures = enrollmentRepository.getEnrollmentsWithLectureByTimetableId(timetableId, ALL).stream()
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

  // TODO : 친구 시간표 조회
  // (userId = 사용자의 ID) or (userId != 사용자의 ID and userId와 사용자의 Id 친구)

  @Transactional
  public void updateTimetableName(Long timetableId, TimetableUpdateRequest timetableUpdateRequest) {
    // TODO : 사용자 ID 가져오는 로직이 필요하다.

    Long userId = 1L;
    Timetable timetable = getTimetableOfUser(userId, timetableId);

    timetable.updateName(timetableUpdateRequest.getName().trim());
  }



  @Transactional
  public void deleteTimetable(Long timetableId) {
    // TODO : 사용자 ID 가져오는 로직이 필요하다.

    Long userId = 1L;
    Timetable timetable = getTimetableOfUser(userId, timetableId);

    List<Long> customLectureIds = enrollmentRepository.getEnrollmentsWithLectureByTimetableId(timetableId, CUSTOM).stream()
        .map(enrollment -> enrollment.getLecture().getId())
        .collect(Collectors.toList());

    enrollmentRepository.deleteByTimetableId(timetableId);
    lectureDetailRepository.deleteLectureDetailsByLectureIds(customLectureIds);
    lectureRepository.deleteLectureByLectureIds(customLectureIds);

    // timetableRepository.delete(getTimetableOfUser(userId, timetableId)); -> 이거 왜 안되지?
    timetableRepository.deleteByTimetableId(timetable.getId());
  }

  private Timetable getTimetableOfUser(Long userId, Long timetableId) {
    return timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId).orElseThrow(() -> new NotFoundException(NOT_FOUND));
  }
}
