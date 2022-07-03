
package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.common.ErrorCode.DUPLICATE_TIMETABLE_NAME;
import static com.prgrms.coretime.common.ErrorCode.NOT_FOUND;
import static com.prgrms.coretime.common.ErrorCode.NOT_FRIEND;
import static com.prgrms.coretime.common.ErrorCode.TIMETABLE_NOT_FOUND;
import static com.prgrms.coretime.common.ErrorCode.USER_NOT_FOUND;
import static com.prgrms.coretime.timetable.domain.repository.enrollment.LectureType.ALL;
import static com.prgrms.coretime.timetable.domain.repository.enrollment.LectureType.CUSTOM;

import com.prgrms.coretime.common.error.exception.DuplicateRequestException;
import com.prgrms.coretime.common.error.exception.InvalidRequestException;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.friend.domain.FriendRepository;
import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.lecture.Lecture;
import com.prgrms.coretime.timetable.domain.repository.enrollment.EnrollmentRepository;
import com.prgrms.coretime.timetable.domain.repository.lecture.LectureRepository;
import com.prgrms.coretime.timetable.domain.repository.lectureDetail.LectureDetailRepository;
import com.prgrms.coretime.timetable.domain.repository.timetable.TimetableRepository;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import com.prgrms.coretime.timetable.dto.request.TimetableCreateRequest;
import com.prgrms.coretime.timetable.dto.request.TimetableUpdateRequest;
import com.prgrms.coretime.timetable.dto.response.FriendDefaultTimetableInfo;
import com.prgrms.coretime.timetable.dto.response.LectureDetailInfo;
import com.prgrms.coretime.timetable.dto.response.LectureInfo;
import com.prgrms.coretime.timetable.dto.response.TimetableInfo;
import com.prgrms.coretime.timetable.dto.response.TimetableResponse;
import com.prgrms.coretime.timetable.dto.response.TimetablesResponse;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimetableService {
  private final TimetableRepository timetableRepository;
  private final EnrollmentRepository enrollmentRepository;
  private final LectureDetailRepository lectureDetailRepository;
  private final LectureRepository lectureRepository;
  private final UserRepository userRepository;
  private final FriendRepository friendRepository;

  @Transactional
  public Long createTimetable(Long userId, TimetableCreateRequest timetableCreateRequest) {
    User user  = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

    String timetableName = timetableCreateRequest.getName().trim();
    Integer year = timetableCreateRequest.getYear();
    Semester semester = timetableCreateRequest.getSemester();

    if(timetableRepository.getTimetableBySameName(userId, timetableName, year, semester).isPresent()) {
      throw new DuplicateRequestException(DUPLICATE_TIMETABLE_NAME);
    }

    Timetable newTimetable = Timetable.builder()
        .name(timetableName)
        .year(year)
        .semester(semester)
        .user(user)
        .isDefault(timetableRepository.isFirstTimetable(userId, year, semester))
        .build();

    Timetable createdTimetable = timetableRepository.save(newTimetable);
    return createdTimetable.getId();
  }

  @Transactional(readOnly = true)
  public TimetablesResponse getTimetables(Long userId, Integer year, Semester semester) {
    List<TimetableInfo> timetables = timetableRepository.getTimetables(userId, year, semester).stream()
        .map(timetable -> new TimetableInfo(timetable.getId(), timetable.getName(), timetable.getIsDefault()))
        .collect(Collectors.toList());

    return new TimetablesResponse(timetables);
  }

  @Transactional(readOnly = true)
  public TimetableResponse getDefaultTimetable(Long userId, Integer year, Semester semester) {
    Timetable defaultTimetable = getDefaultTimetableOfUser(userId, year, semester);
    List<LectureInfo> enrollmentedLectures = getEnrollmentedLectures(defaultTimetable.getId());

    return TimetableResponse.builder()
        .timetableId(defaultTimetable.getId())
        .name(defaultTimetable.getName())
        .year(defaultTimetable.getYear())
        .semester(defaultTimetable.getSemester())
        .isDefault(defaultTimetable.getIsDefault())
        .lectures(enrollmentedLectures)
        .build();
  }

  @Transactional(readOnly = true)
  public TimetableResponse getTimetable(Long userId, Long timetableId) {
    Timetable timetable = getTimetableOfUser(userId, timetableId);
    List<LectureInfo> enrollmentedLectures = getEnrollmentedLectures(timetable.getId());

    return TimetableResponse.builder()
        .timetableId(timetable.getId())
        .name(timetable.getName())
        .year(timetable.getYear())
        .semester(timetable.getSemester())
        .isDefault(timetable.getIsDefault())
        .lectures(enrollmentedLectures)
        .build();
  }

  @Transactional
  public List<FriendDefaultTimetableInfo> getFriendDefaultTimetableInfos(Long userId, Long friendId) {
    validateFriendRelationship(userId, friendId);

    return timetableRepository.getDefaultTimetables(
            friendId).stream()
        .map(timetable -> new FriendDefaultTimetableInfo(timetable.getYear(), timetable.getSemester()))
        .sorted((o1, o2) -> compare(o1, o2))
        .toList();
  }

  @Transactional
  public List<LectureInfo> getDefaultTimetableOfFriend(Long userId, Long friendId, int year, Semester semester) {
    validateFriendRelationship(userId, friendId);

    Timetable friendDefaultTimetable = getDefaultTimetableOfUser(userId, year, semester);

    return getEnrollmentedLectures(friendDefaultTimetable.getId());
  }

  @Transactional
  public void updateTimetable(Long userId, Long timetableId, TimetableUpdateRequest timetableUpdateRequest) {
    Timetable timetable = getTimetableOfUser(userId, timetableId);

    String updatedTimetableName = timetableUpdateRequest.getName().trim();
    Integer year = timetable.getYear();
    Semester semester = timetable.getSemester();
    Boolean updatedIsDefault = timetableUpdateRequest.getIsDefault();

    Timetable sameNameTable = timetableRepository.getTimetableBySameName(userId, updatedTimetableName, year, semester).orElse(timetable);
    if(timetable != sameNameTable) {
      throw new DuplicateRequestException(DUPLICATE_TIMETABLE_NAME);
    }

    timetable.updateName(updatedTimetableName.trim());

    if(updatedIsDefault) {
      Timetable preDefaultTimetable = getDefaultTimetableOfUser(userId, timetable.getYear(), timetable.getSemester());
      preDefaultTimetable.makeNonDefault();
      timetable.makeDefault();
    }
  }

  @Transactional
  public void deleteTimetable(Long userId, Long timetableId) {
    Timetable timetable = getTimetableOfUser(userId, timetableId);

    List<Long> customLectureIds = enrollmentRepository.getEnrollmentsWithLectureByTimetableId(timetable.getId(), CUSTOM).stream()
        .map(enrollment -> enrollment.getLecture().getId())
        .collect(Collectors.toList());

    enrollmentRepository.deleteByTimetableId(timetable.getId());
    lectureDetailRepository.deleteLectureDetailsByLectureIds(customLectureIds);
    lectureRepository.deleteLectureByLectureIds(customLectureIds);
    timetableRepository.deleteByTimetableId(timetable.getId());

    if(timetable.getIsDefault()) {
      timetableRepository.getRecentlyAddedTimetable(userId, timetable.getYear(), timetable.getSemester())
          .ifPresent(newDefaultTimetable -> newDefaultTimetable.makeDefault());
    }
  }

  private Timetable getDefaultTimetableOfUser(Long userId, Integer year, Semester semester) {
    return timetableRepository.getDefaultTimetable(userId, year, semester).orElseThrow(() -> new NotFoundException(TIMETABLE_NOT_FOUND));
  }

  private Timetable getTimetableOfUser(Long userId, Long timetableId) {
    return timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId).orElseThrow(() -> new NotFoundException(TIMETABLE_NOT_FOUND));
  }

  private void validateFriendRelationship(Long userId, Long friendId) {
    if(!friendRepository.existsFriendRelationship(userId, friendId)) {
      throw new InvalidRequestException(NOT_FRIEND);
    }
  }

  private List<LectureInfo> getEnrollmentedLectures(Long timetableId) {
    return enrollmentRepository.getEnrollmentsWithLectureByTimetableId(timetableId, ALL).stream()
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
  }

  private int compare(FriendDefaultTimetableInfo o1, FriendDefaultTimetableInfo o2) {
    if (o1.getYear() == o2.getYear()) {
      return o2.getSemester().getOrder() - o1.getSemester().getOrder() ;
    } else {
      return o2.getYear() - o1.getYear();
    }
  }
}
