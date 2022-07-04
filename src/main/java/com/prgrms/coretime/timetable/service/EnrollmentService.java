package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.common.ErrorCode.ALREADY_ADDED_LECTURE;
import static com.prgrms.coretime.common.ErrorCode.INVALID_LECTURE_ADD_REQUEST;
import static com.prgrms.coretime.common.ErrorCode.LECTURE_TIME_OVERLAP;
import static com.prgrms.coretime.common.ErrorCode.NOT_FOUND;

import com.prgrms.coretime.common.error.exception.AlreadyExistsException;
import com.prgrms.coretime.common.error.exception.InvalidRequestException;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.timetable.domain.enrollment.Enrollment;
import com.prgrms.coretime.timetable.domain.enrollment.EnrollmentId;
import com.prgrms.coretime.timetable.domain.lecture.CustomLecture;
import com.prgrms.coretime.timetable.domain.lecture.Lecture;
import com.prgrms.coretime.timetable.domain.lecture.OfficialLecture;
import com.prgrms.coretime.timetable.domain.lectureDetail.LectureDetail;
import com.prgrms.coretime.timetable.domain.repository.enrollment.EnrollmentRepository;
import com.prgrms.coretime.timetable.domain.repository.lecture.LectureRepository;
import com.prgrms.coretime.timetable.domain.repository.lectureDetail.LectureDetailRepository;
import com.prgrms.coretime.timetable.domain.repository.timetable.TimetableRepository;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import com.prgrms.coretime.timetable.dto.request.CustomLectureDetail;
import com.prgrms.coretime.timetable.dto.request.CustomLectureRequest;
import com.prgrms.coretime.timetable.dto.request.EnrollmentCreateRequest;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {
  private final TimetableRepository timetableRepository;
  private final LectureRepository lectureRepository;
  private final LectureDetailRepository lectureDetailRepository;
  private final EnrollmentRepository enrollmentRepository;

  @Transactional
  public Enrollment addOfficialLectureToTimetable(Long userId, Long schoolId, Long timetableId, EnrollmentCreateRequest enrollmentCreateRequest) {
    Timetable timetable = getTimetableOfUser(userId, timetableId);
    OfficialLecture officialLecture = lectureRepository.getOfficialLectureById(enrollmentCreateRequest.getLectureId()).orElseThrow(() -> new NotFoundException(NOT_FOUND));

    if(schoolId != officialLecture.getSchool().getId() || !timetable.getYear().equals(officialLecture.getOpenYear()) || !timetable.getSemester().equals(officialLecture.getSemester())) {
      throw new InvalidRequestException(INVALID_LECTURE_ADD_REQUEST);
    }

    Enrollment enrollment = new Enrollment(officialLecture, timetable);

    if(enrollmentRepository.findById(enrollment.getEnrollmentId()).isPresent()) {
      throw new AlreadyExistsException(ALREADY_ADDED_LECTURE);
    }

    validateLectureTimeOverlap(timetable.getId(), officialLecture.getLectureDetails());

    return enrollmentRepository.save(enrollment);
  }

  @Transactional
  public Enrollment addCustomLectureToTimetable(Long timetableId, CustomLectureRequest customLectureRequest) {
    // TODO : 사용자 ID 가져오는 로직이 필요하다.

    Long userId = 1L;
    Timetable timetable = getTimetableOfUser(userId, timetableId);

    List<LectureDetail> lectureDetails = changeCustomLectureDetailsToLectureDetails(customLectureRequest);

    validateLectureTimeOverlap(timetable.getId(), lectureDetails);

    Lecture customLecture = lectureRepository.save(CustomLecture.builder()
        .name(customLectureRequest.getName())
        .professor(customLectureRequest.getProfessor())
        .classroom(customLectureRequest.getClassroom())
        .build());

    createLectureDetails(customLecture, lectureDetails);

    Enrollment enrollment = new Enrollment(customLecture, timetable);

    return enrollmentRepository.save(enrollment);
  }

  @Transactional
  public void updateCustomLecture(Long timetableId, Long lectureId, CustomLectureRequest customLectureRequest) {
    // TODO : 사용자 ID 가져오는 로직이 필요하다.

    Long userId = 1L;
    Timetable timetable = getTimetableOfUser(userId, timetableId);

    Lecture customLecture = lectureRepository.findById(lectureId).orElseThrow(() -> new NotFoundException(NOT_FOUND));
    List<LectureDetail> lectureDetails = changeCustomLectureDetailsToLectureDetails(customLectureRequest);

    customLecture.updateName(customLectureRequest.getName());
    customLecture.updateProfessor(customLectureRequest.getProfessor());
    customLecture.updateClassroom(customLectureRequest.getClassroom());

    lectureDetailRepository.deleteCustomLectureDetailsByLectureId(customLecture.getId());

    validateLectureTimeOverlap(timetable.getId(), lectureDetails);

    createLectureDetails(customLecture, lectureDetails);
  }

  @Transactional
  public void deleteLectureFromTimetable(Long timetableId, Long lectureId) {
    // TODO : 사용자 ID 가져오는 로직이 필요하다.

    Long userId = 1L;
    Timetable timetable = getTimetableOfUser(userId, timetableId);

    EnrollmentId enrollmentId = new EnrollmentId(lectureId, timetable.getId());
    Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(() -> new NotFoundException(NOT_FOUND));
    enrollmentRepository.delete(enrollment);

    if(lectureRepository.isCustomLecture(lectureId)) {
      lectureDetailRepository.deleteCustomLectureDetailsByLectureId(lectureId);
      lectureRepository.deleteById(lectureId);
    }
  }

  private Timetable getTimetableOfUser(Long userId, Long timetableId) {
    return timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId).orElseThrow(() -> new NotFoundException(NOT_FOUND));
  }

  private void validateLectureTimeOverlap(Long timetableId, List<LectureDetail> lectureDetails) {
    if(lectureRepository.getNumberOfConflictLectures(timetableId, lectureDetails) > 0) {
      throw new InvalidRequestException(LECTURE_TIME_OVERLAP);
    }
  }

  private List<LectureDetail> changeCustomLectureDetailsToLectureDetails(CustomLectureRequest customLectureRequest) {
    Set<CustomLectureDetail> lectureDetailSet = new HashSet<>();

    return customLectureRequest.getLectureDetails().stream()
        .map(customLectureDetail -> {
          if(lectureDetailSet.contains(customLectureDetail)) {
            throw new IllegalArgumentException("입력된 시간중 겹치는 시간이 있습니다.");
          }

          lectureDetailSet.add(customLectureDetail);
          return LectureDetail.builder()
            .startTime(LocalTime.parse(customLectureDetail.getStartTime()))
            .endTime(LocalTime.parse(customLectureDetail.getEndTime()))
            .day(customLectureDetail.getDay())
            .build();
        })
        .collect(Collectors.toList());
  }

  private void createLectureDetails(Lecture customLecture, List<LectureDetail> lectureDetails) {
    for(LectureDetail lectureDetail : lectureDetails) {
      lectureDetail.setLecture(customLecture);
      lectureDetailRepository.save(lectureDetail);
    }
  }
}
