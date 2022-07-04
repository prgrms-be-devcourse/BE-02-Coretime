package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.common.ErrorCode.ALREADY_ADDED_LECTURE;
import static com.prgrms.coretime.common.ErrorCode.INVALID_LECTURE_ADD_REQUEST;
import static com.prgrms.coretime.common.ErrorCode.LECTURE_DETAIL_TIME_OVERLAP;
import static com.prgrms.coretime.common.ErrorCode.LECTURE_NOT_FOUND;
import static com.prgrms.coretime.common.ErrorCode.LECTURE_TIME_OVERLAP;
import static com.prgrms.coretime.common.ErrorCode.NOT_FOUND;
import static com.prgrms.coretime.common.ErrorCode.TIMETABLE_NOT_FOUND;

import com.prgrms.coretime.common.error.exception.AlreadyExistsException;
import com.prgrms.coretime.common.error.exception.InvalidRequestException;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.timetable.domain.Enrollment;
import com.prgrms.coretime.timetable.domain.EnrollmentId;
import com.prgrms.coretime.timetable.domain.CustomLecture;
import com.prgrms.coretime.timetable.domain.Lecture;
import com.prgrms.coretime.timetable.domain.OfficialLecture;
import com.prgrms.coretime.timetable.domain.LectureDetail;
import com.prgrms.coretime.timetable.domain.repository.enrollment.EnrollmentRepository;
import com.prgrms.coretime.timetable.domain.repository.lecture.LectureRepository;
import com.prgrms.coretime.timetable.domain.repository.lectureDetail.LectureDetailRepository;
import com.prgrms.coretime.timetable.domain.repository.timetable.TimetableRepository;
import com.prgrms.coretime.timetable.domain.Timetable;
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
    Timetable timetable = getTimetableOfUser(userId, timetableId); // O
    OfficialLecture officialLecture = lectureRepository.getOfficialLectureById(enrollmentCreateRequest.getLectureId()).orElseThrow(() -> new NotFoundException(LECTURE_NOT_FOUND)); // O

    // 1. 엔티티 내부에서 처리해도 괜찮지 않느냐?
    // 2. @Component를 이용해라
    if(!officialLecture.canEnrol(schoolId)) {
      throw new InvalidRequestException(INVALID_LECTURE_ADD_REQUEST);
    }
    if(!timetable.canEnrol(officialLecture.getOpenYear(), officialLecture.getSemester())) {
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
  public Enrollment addCustomLectureToTimetable(Long userId, Long timetableId, CustomLectureRequest customLectureRequest) {
    Timetable timetable = getTimetableOfUser(userId, timetableId);

    List<LectureDetail> lectureDetails = changeCustomLectureDetailsToLectureDetails(customLectureRequest);

    validateLectureTimeOverlap(timetable.getId(), lectureDetails);

    Lecture customLecture = lectureRepository.save(
        CustomLecture.builder()
            .name(customLectureRequest.getName())
            .professor(customLectureRequest.getProfessor())
            .classroom(customLectureRequest.getClassroom())
            .build()
    );

    createLectureDetails(customLecture, lectureDetails);

    return enrollmentRepository.save(new Enrollment(customLecture, timetable));
  }

  @Transactional
  public void updateCustomLecture(Long userId, Long timetableId, Long lectureId, CustomLectureRequest customLectureRequest) {
    Timetable timetable = getTimetableOfUser(userId, timetableId);

    Lecture customLecture = lectureRepository.findById(lectureId).orElseThrow(() -> new NotFoundException(LECTURE_NOT_FOUND));
    List<LectureDetail> lectureDetails = changeCustomLectureDetailsToLectureDetails(customLectureRequest);

    customLecture.updateName(customLectureRequest.getName());
    customLecture.updateProfessor(customLectureRequest.getProfessor());
    customLecture.updateClassroom(customLectureRequest.getClassroom());

    lectureDetailRepository.deleteCustomLectureDetailsByLectureId(customLecture.getId());

    validateLectureTimeOverlap(timetable.getId(), lectureDetails);

    createLectureDetails(customLecture, lectureDetails);
  }

  @Transactional
  public void deleteLectureFromTimetable(Long userId, Long timetableId, Long lectureId) {
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
    return timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId).orElseThrow(() -> new NotFoundException(TIMETABLE_NOT_FOUND));
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
            throw new InvalidRequestException(LECTURE_DETAIL_TIME_OVERLAP);
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
