package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.common.ErrorCode.NOT_FOUND;

import com.prgrms.coretime.common.error.NotFoundException;
import com.prgrms.coretime.timetable.domain.enrollment.Enrollment;
import com.prgrms.coretime.timetable.domain.enrollment.EnrollmentId;
import com.prgrms.coretime.timetable.domain.lecture.OfficialLecture;
import com.prgrms.coretime.timetable.domain.lectureDetail.LectureDetail;
import com.prgrms.coretime.timetable.domain.repository.enrollment.EnrollmentRepository;
import com.prgrms.coretime.timetable.domain.repository.lecture.LectureRepository;
import com.prgrms.coretime.timetable.domain.repository.timetable.TimetableRepository;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import com.prgrms.coretime.timetable.dto.request.EnrollmentCreateRequest;
import java.util.List;
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
  private final EnrollmentRepository enrollmentRepository;

  @Transactional
  public Enrollment addOfficialLectureToTimetable(Long timetableId, EnrollmentCreateRequest enrollmentCreateRequest) {
    Timetable timetable = timetableRepository.findById(timetableId).orElseThrow(() -> new NotFoundException(NOT_FOUND));
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

  private void validateLectureConflict(Long timetableId, List<LectureDetail> lectureDetails) {
    if(lectureRepository.getNumberOfConflictLectures(timetableId, lectureDetails) > 0) {
      throw new IllegalArgumentException("같은 시간에 다른 강의가 있습니다.");
    }
  }
}
