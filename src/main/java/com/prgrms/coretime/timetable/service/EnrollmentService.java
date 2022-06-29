package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.common.ErrorCode.NOT_FOUND;

import com.prgrms.coretime.common.error.NotFoundException;
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
  public Enrollment addOfficialLectureToTimetable(Long timetableId, EnrollmentCreateRequest enrollmentCreateRequest) {
    // TODO : 시간표가 사용자의 것인지 확인하는 로직이 필요하다.
    // TODO : 사용자가 속한 학교와 강의가 속한 학교를 비교해야한다.(같은 학교인 경우에만 시간표에 강의를 추가할 수 있다.)

    Timetable timetable = getTimetableById(timetableId);
    OfficialLecture officialLecture = lectureRepository.findOfficialLectureById(enrollmentCreateRequest.getLectureId()).orElseThrow(() -> new NotFoundException(NOT_FOUND));

    if(!timetable.getYear().equals(officialLecture.getOpenYear()) || !timetable.getSemester().equals(officialLecture.getSemester())) {
      throw new IllegalArgumentException("시간표에 추가할 수 없는 강의입니다.");
    }

    EnrollmentId enrollmentId = new EnrollmentId(officialLecture.getId(), timetable.getId());
    if(enrollmentRepository.findById(enrollmentId).isPresent()) {
      throw new IllegalArgumentException("이미 추가된 강의입니다.");
    }

    validateLectureConflict(timetable.getId(), officialLecture.getLectureDetails());

    Enrollment enrollment = new Enrollment(enrollmentId);
    enrollment.setLecture(officialLecture);
    enrollment.setTimeTable(timetable);

    return enrollmentRepository.save(enrollment);
  }

  @Transactional
  public Enrollment addCustomLectureToTimetable(Long timetableId, CustomLectureRequest customLectureRequest) {
    // TODO : 시간표가 사용자의 것인지 확인하는 로직이 필요하다.

    Timetable timetable = getTimetableById(timetableId);
    List<LectureDetail> lectureDetails = changeCustomLectureDetailsToLectureDetails(customLectureRequest);

    validateLectureConflict(timetable.getId(), lectureDetails);

    Lecture customLecture = lectureRepository.save(CustomLecture.builder()
        .name(customLectureRequest.getName())
        .professor(customLectureRequest.getProfessor())
        .classroom(customLectureRequest.getClassroom())
        .build());

    createLectureDetails(customLecture, lectureDetails);

    EnrollmentId enrollmentId = new EnrollmentId(customLecture.getId(), timetable.getId());
    Enrollment enrollment = new Enrollment(enrollmentId);
    enrollment.setLecture(customLecture);
    enrollment.setTimeTable(timetable);

    return enrollmentRepository.save(enrollment);
  }

  @Transactional
  public void updateCustomLecture(Long timetableId, Long lectureId, CustomLectureRequest customLectureRequest) {
    // TODO : 시간표가 사용자의 것인지 확인하는 로직이 필요하다.

    Timetable timetable = getTimetableById(timetableId);
    Lecture customLecture = lectureRepository.findById(lectureId).orElseThrow(() -> new NotFoundException(NOT_FOUND));
    List<LectureDetail> lectureDetails = changeCustomLectureDetailsToLectureDetails(customLectureRequest);

    customLecture.updateName(customLectureRequest.getName());
    customLecture.updateProfessor(customLectureRequest.getProfessor());
    customLecture.updateClassroom(customLectureRequest.getClassroom());

    lectureDetailRepository.deleteCustomLectureDetailsByLectureId(customLecture.getId());

    validateLectureConflict(timetable.getId(), lectureDetails);

    createLectureDetails(customLecture, lectureDetails);
  }

  @Transactional
  public void deleteLectureFromTimetable(Long timetableId, Long lectureId) {
    // TODO : 시간표가 사용자의 것인지 확인하는 로직이 필요하다.

    EnrollmentId enrollmentId = new EnrollmentId(lectureId, timetableId);
    Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(() -> new NotFoundException(NOT_FOUND));
    enrollmentRepository.delete(enrollment);

    if(lectureRepository.isCustomLecture(lectureId)) {
      lectureDetailRepository.deleteCustomLectureDetailsByLectureId(lectureId);
      lectureRepository.deleteById(lectureId);
    }
  }

  private Timetable getTimetableById(Long timetableId) {
    return timetableRepository.findById(timetableId).orElseThrow(() -> new NotFoundException(NOT_FOUND));
  }

  private void validateLectureConflict(Long timetableId, List<LectureDetail> lectureDetails) {
    if(lectureRepository.getNumberOfConflictLectures(timetableId, lectureDetails) > 0) {
      throw new IllegalArgumentException("같은 시간에 다른 강의가 있습니다.");
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
