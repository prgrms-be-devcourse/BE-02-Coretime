package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.common.ErrorCode.ENROLLMENT_NOT_FOUND;
import static com.prgrms.coretime.common.ErrorCode.LECTURE_DETAIL_TIME_OVERLAP;
import static com.prgrms.coretime.common.ErrorCode.LECTURE_NOT_FOUND;
import static com.prgrms.coretime.common.ErrorCode.TIMETABLE_NOT_FOUND;

import com.prgrms.coretime.common.error.exception.InvalidRequestException;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.timetable.domain.CustomLecture;
import com.prgrms.coretime.timetable.domain.Enrollment;
import com.prgrms.coretime.timetable.domain.EnrollmentId;
import com.prgrms.coretime.timetable.domain.Lecture;
import com.prgrms.coretime.timetable.domain.LectureDetail;
import com.prgrms.coretime.timetable.domain.OfficialLecture;
import com.prgrms.coretime.timetable.domain.Timetable;
import com.prgrms.coretime.timetable.domain.repository.enrollment.EnrollmentRepository;
import com.prgrms.coretime.timetable.domain.repository.lecture.LectureRepository;
import com.prgrms.coretime.timetable.domain.repository.lectureDetail.LectureDetailRepository;
import com.prgrms.coretime.timetable.domain.repository.timetable.TimetableRepository;
import com.prgrms.coretime.timetable.dto.request.CustomLectureDetail;
import com.prgrms.coretime.timetable.dto.request.CustomLectureRequest;
import com.prgrms.coretime.timetable.dto.request.EnrollmentCreateRequest;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
  private final EnrollmentRepository enrollmentRepository;
  private final EnrollmentValidator enrollmentValidator;
  private final TimetableRepository timetableRepository;
  private final LectureRepository lectureRepository;
  private final LectureDetailRepository lectureDetailRepository;

  @Transactional
  public EnrollmentId addOfficialLectureToTimetable(Long userId, Long schoolId, Long timetableId, EnrollmentCreateRequest enrollmentCreateRequest) {
    Timetable timetable = getTimetableOfUser(userId, timetableId);
    OfficialLecture officialLecture = lectureRepository.getOfficialLectureById(enrollmentCreateRequest.getLectureId()).orElseThrow(() -> new NotFoundException(LECTURE_NOT_FOUND));

    enrollmentValidator.validateOfficialLectureEnrollment(schoolId, officialLecture, timetable);
    enrollmentValidator.validateLectureTimeOverlap(timetable.getId(), officialLecture.getLectureDetails());

    return enrollmentRepository.save(new Enrollment(officialLecture, timetable)).getEnrollmentId();
  }

  @Transactional
  public EnrollmentId addCustomLectureToTimetable(Long userId, Long timetableId, CustomLectureRequest customLectureRequest) {
    Timetable timetable = getTimetableOfUser(userId, timetableId);
    List<LectureDetail> lectureDetails = changeCustomLectureDetailsToLectureDetails(customLectureRequest.getLectureDetails());

    enrollmentValidator.validateLectureTimeOverlap(timetable.getId(), lectureDetails);

    Lecture customLecture = lectureRepository.save(
        CustomLecture.builder()
            .name(customLectureRequest.getName())
            .professor(customLectureRequest.getProfessor())
            .classroom(customLectureRequest.getClassroom())
            .build()
    );
    createLectureDetails(customLecture, lectureDetails);

    return enrollmentRepository.save(new Enrollment(customLecture, timetable)).getEnrollmentId();
  }

  @Transactional
  public void updateCustomLecture(Long userId, Long timetableId, Long lectureId, CustomLectureRequest customLectureRequest) {
    Timetable timetable = getTimetableOfUser(userId, timetableId);
    Lecture customLecture = lectureRepository.findById(lectureId).orElseThrow(() -> new NotFoundException(LECTURE_NOT_FOUND));
    List<Long> lectureDetailIds = customLecture.getLectureDetails().stream()
        .map(lectureDetail -> lectureDetail.getId())
        .collect(Collectors.toList());
    List<LectureDetail> lectureDetails = changeCustomLectureDetailsToLectureDetails(customLectureRequest.getLectureDetails());

    enrollmentValidator.validateLectureTimeOverlap(timetable.getId(), lectureDetails, lectureDetailIds);

    customLecture.updateLecture(customLectureRequest.getName(), customLectureRequest.getProfessor(), customLectureRequest.getClassroom());

    lectureDetailRepository.deleteCustomLectureDetailsByLectureId(customLecture.getId());
    createLectureDetails(customLecture, lectureDetails);
  }

  @Transactional
  public void deleteLectureFromTimetable(Long userId, Long timetableId, Long lectureId) {
    Timetable timetable = getTimetableOfUser(userId, timetableId);
    Enrollment enrollment = enrollmentRepository.findById(new EnrollmentId(lectureId, timetable.getId())).orElseThrow(() -> new NotFoundException(ENROLLMENT_NOT_FOUND));

    enrollmentRepository.delete(enrollment);

    if(lectureRepository.isCustomLecture(lectureId)) {
      lectureDetailRepository.deleteCustomLectureDetailsByLectureId(lectureId);
      lectureRepository.deleteById(lectureId);
    }
  }

  private Timetable getTimetableOfUser(Long userId, Long timetableId) {
    return timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId).orElseThrow(() -> new NotFoundException(TIMETABLE_NOT_FOUND));
  }

  private List<LectureDetail> changeCustomLectureDetailsToLectureDetails(List<CustomLectureDetail> customLectureDetails) {
    Set<CustomLectureDetail> lectureDetailSet = new HashSet<>();

    return customLectureDetails.stream()
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
