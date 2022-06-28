package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.common.ErrorCode.NOT_FOUND;

import com.prgrms.coretime.common.error.NotFoundException;
import com.prgrms.coretime.timetable.domain.enrollment.Enrollment;
import com.prgrms.coretime.timetable.domain.enrollment.EnrollmentId;
import com.prgrms.coretime.timetable.domain.lecture.CustomLecture;
import com.prgrms.coretime.timetable.domain.lecture.Lecture;
import com.prgrms.coretime.timetable.domain.lecture.OfficialLecture;
import com.prgrms.coretime.timetable.domain.lectureDetail.LectureDetail;
import com.prgrms.coretime.timetable.domain.repository.LectureDetailRepository;
import com.prgrms.coretime.timetable.domain.repository.enrollment.EnrollmentRepository;
import com.prgrms.coretime.timetable.domain.repository.lecture.LectureRepository;
import com.prgrms.coretime.timetable.domain.repository.timetable.TimetableRepository;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import com.prgrms.coretime.timetable.dto.request.CustomLectureCreateRequest;
import com.prgrms.coretime.timetable.dto.request.EnrollmentCreateRequest;
import java.time.LocalTime;
import java.util.List;
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
    Timetable timetable = getTimetableById(timetableId);
    OfficialLecture officialLecture = lectureRepository.findOfficialLectureById(enrollmentCreateRequest.getLectureId()).orElseThrow(() -> new NotFoundException(NOT_FOUND));

    // TODO : 학교 정보 비교
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
  public Enrollment addCustomLectureToTimetable(Long timetableId, CustomLectureCreateRequest customLectureCreateRequest) {
    Timetable timetable = getTimetableById(timetableId);

    List<LectureDetail> lectureDetails = customLectureCreateRequest.getLectureDetails().stream()
        .map(customLectureDetail -> LectureDetail.builder()
            .startTime(LocalTime.parse(customLectureDetail.getStartTime()))
            .endTime(LocalTime.parse(customLectureDetail.getEndTime()))
            .day(customLectureDetail.getDay())
            .build())
        .collect(Collectors.toList());

    validateLectureConflict(timetable.getId(), lectureDetails);

    Lecture customLecture = lectureRepository.save(CustomLecture.builder()
        .name(customLectureCreateRequest.getName())
        .professor(customLectureCreateRequest.getProfessor())
        .classroom(customLectureCreateRequest.getClassRoom())
        .build());

    for(LectureDetail lectureDetail : lectureDetails) {
      lectureDetail.setLecture(customLecture);
      lectureDetailRepository.save(lectureDetail);
    }

    EnrollmentId enrollmentId = new EnrollmentId(customLecture.getId(), timetable.getId());
    Enrollment enrollment = new Enrollment(enrollmentId);
    enrollment.setLecture(customLecture);
    enrollment.setTimeTable(timetable);

    return enrollmentRepository.save(enrollment);
  }

  // custom 강의 수정

  // 강의 시간표에서 삭제

  private Timetable getTimetableById(Long timetableId) {
    return timetableRepository.findById(timetableId).orElseThrow(() -> new NotFoundException(NOT_FOUND));
  }

  private void validateLectureConflict(Long timetableId, List<LectureDetail> lectureDetails) {
    long cnt = lectureRepository.getNumberOfConflictLectures(timetableId, lectureDetails);
    log.info("cnt : {}", cnt);
    if(cnt > 0) {
      throw new IllegalArgumentException("같은 시간에 다른 강의가 있습니다.");
    }
  }
}
