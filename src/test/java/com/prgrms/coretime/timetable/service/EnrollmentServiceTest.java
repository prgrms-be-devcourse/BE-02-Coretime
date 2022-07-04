package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.common.ErrorCode.LECTURE_NOT_FOUND;
import static com.prgrms.coretime.common.ErrorCode.TIMETABLE_NOT_FOUND;
import static com.prgrms.coretime.timetable.domain.Semester.FIRST;
import static com.prgrms.coretime.timetable.domain.Semester.SECOND;
import static com.prgrms.coretime.timetable.domain.Grade.ETC;
import static com.prgrms.coretime.timetable.domain.LectureType.MAJOR;
import static com.prgrms.coretime.timetable.domain.Day.MON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.coretime.common.error.exception.AlreadyExistsException;
import com.prgrms.coretime.common.error.exception.InvalidRequestException;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.timetable.domain.Enrollment;
import com.prgrms.coretime.timetable.domain.CustomLecture;
import com.prgrms.coretime.timetable.domain.OfficialLecture;
import com.prgrms.coretime.timetable.domain.repository.enrollment.EnrollmentRepository;
import com.prgrms.coretime.timetable.domain.repository.lecture.LectureRepository;
import com.prgrms.coretime.timetable.domain.repository.lectureDetail.LectureDetailRepository;
import com.prgrms.coretime.timetable.domain.repository.timetable.TimetableRepository;
import com.prgrms.coretime.timetable.domain.Timetable;
import com.prgrms.coretime.timetable.dto.request.CustomLectureDetail;
import com.prgrms.coretime.timetable.dto.request.CustomLectureRequest;
import com.prgrms.coretime.timetable.dto.request.EnrollmentCreateRequest;
import com.prgrms.coretime.user.domain.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {
  @Mock
  private TimetableRepository timetableRepository;
  @Mock
  private LectureRepository lectureRepository;
  @Mock
  private LectureDetailRepository lectureDetailRepository;
  @Mock
  private EnrollmentRepository enrollmentRepository;
  @InjectMocks
  private EnrollmentService enrollmentService;

  private Long userId, schoolId, timetableId, idOfOfficialLectureFirst, idOfOfficialLectureSecond, customLectureId;
  private User user;
  private School school;
  private Timetable timetable;
  private OfficialLecture officialLectureFirst, officialLectureSecond;
  private CustomLecture customLecture;

  @BeforeEach
  void setUp() {
    userId = 1L;
    user = new User("a@school.com", "testerA");

    schoolId = 2L;
    school = new School("상상대학교", "sangsang.ac.kr");
    school.setId(schoolId);

    timetableId = 3L;
    timetable = Timetable.builder()
        .name("시간표1")
        .year(2022)
        .semester(FIRST)
        .user(user)
        .isDefault(true)
        .build();

    idOfOfficialLectureFirst = 4L;
    officialLectureFirst = OfficialLecture.builder()
        .name("강의1")
        .professor("교수1")
        .classroom("test101")
        .semester(FIRST)
        .openYear(2022)
        .grade(ETC)
        .credit(4.0)
        .code("T101")
        .lectureType(MAJOR)
        .build();
    officialLectureFirst.setSchool(school);

    idOfOfficialLectureSecond = 5L;
    officialLectureSecond = OfficialLecture.builder()
        .name("강의2")
        .professor("교수2")
        .classroom("test102")
        .semester(SECOND)
        .openYear(2022)
        .grade(ETC)
        .credit(4.0)
        .code("T102")
        .lectureType(MAJOR)
        .build();
    officialLectureSecond.setSchool(school);

    customLectureId = 6L;
    customLecture = CustomLecture.builder()
        .name("custom 강의")
        .build();
  }

  @Nested
  @DisplayName("addOfficialLectureToTimetable() 테스트")
  class AddOfficialLectureToTimetableTest {
    private EnrollmentCreateRequest enrollmentCreateRequestFirst = new EnrollmentCreateRequest(idOfOfficialLectureFirst);
    private EnrollmentCreateRequest enrollmentCreateRequestSecond = new EnrollmentCreateRequest(idOfOfficialLectureSecond);

    @Test
    @DisplayName("시간표를 찾지 못한 경우 테스트")
    void testTimetableNotFound() {
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenThrow(new NotFoundException(TIMETABLE_NOT_FOUND));

      try {
        enrollmentService.addOfficialLectureToTimetable(userId, schoolId, timetableId, enrollmentCreateRequestFirst);
      }catch (NotFoundException e) {
        verify(lectureRepository, never()).getOfficialLectureById(enrollmentCreateRequestFirst.getLectureId());
        verify(enrollmentRepository, never()).findById(any());
        verify(lectureRepository, never()).getNumberOfConflictLectures(any(), any());
        verify(enrollmentRepository, never()).save(any());
      }
    }

    @Test
    @DisplayName("강의를 찾지 못한 경우 테스트")
    void testLectureNotFound() {
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenReturn(
          Optional.of(timetable));
      when(lectureRepository.getOfficialLectureById(enrollmentCreateRequestFirst.getLectureId())).thenThrow(new NotFoundException(LECTURE_NOT_FOUND));

      try {
        enrollmentService.addOfficialLectureToTimetable(userId, schoolId, timetableId, enrollmentCreateRequestFirst);
      }catch (NotFoundException e) {
        verify(enrollmentRepository, never()).findById(any());
        verify(lectureRepository, never()).getNumberOfConflictLectures(any(), any());
        verify(enrollmentRepository, never()).save(any());
      }
    }

    @Test
    @DisplayName("강의는 있으나 시간표와 학기가 맞지 않는 경우 테스트")
    void testLectureDifferentSemester() {
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenReturn(Optional.of(timetable));
      when(lectureRepository.getOfficialLectureById(enrollmentCreateRequestSecond.getLectureId())).thenReturn(Optional.of(officialLectureSecond));

      try {
        enrollmentService.addOfficialLectureToTimetable(userId, schoolId, timetableId, enrollmentCreateRequestSecond);
      }catch (InvalidRequestException e) {
        verify(enrollmentRepository, never()).findById(any());
        verify(lectureRepository, never()).getNumberOfConflictLectures(any(), any());
        verify(enrollmentRepository, never()).save(any());
      }
    }

    @Test
    @DisplayName("이미 시간표에 추가된 강의인 경우 테스트")
    void testAlreadyAddedLecture() {
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenReturn(Optional.of(timetable));
      when(lectureRepository.getOfficialLectureById(enrollmentCreateRequestFirst.getLectureId())).thenReturn(Optional.of(officialLectureFirst));
      when(enrollmentRepository.findById(any())).thenReturn(Optional.of(new Enrollment(officialLectureFirst, timetable)));

      try {
        enrollmentService.addOfficialLectureToTimetable(userId, schoolId, timetableId, enrollmentCreateRequestFirst);
      }catch (AlreadyExistsException e) {
        verify(lectureRepository, never()).getNumberOfConflictLectures(any(), any());
        verify(enrollmentRepository, never()).save(any());
      }
    }

    @Test
    @DisplayName("시간표에 추가할 강의가 이미 추가된 강의와 시간이 겹치는 경우 테스트")
    void testLectureTimeOverLap() {
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenReturn(Optional.of(timetable));
      when(lectureRepository.getOfficialLectureById(enrollmentCreateRequestFirst.getLectureId())).thenReturn(Optional.of(officialLectureFirst));
      when(enrollmentRepository.findById(any())).thenReturn(Optional.empty());
      when(lectureRepository.getNumberOfConflictLectures(any(), any())).thenReturn(1L);

      try {
        enrollmentService.addOfficialLectureToTimetable(userId, schoolId, timetableId, enrollmentCreateRequestFirst);
      }catch (InvalidRequestException e) {
        verify(enrollmentRepository, never()).save(any());
      }
    }

    @Test
    @DisplayName("시간표에 강의가 추가될 수 있는 경우 테스트")
    void testEnrollmentSave() {
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenReturn(Optional.of(timetable));
      when(lectureRepository.getOfficialLectureById(enrollmentCreateRequestFirst.getLectureId())).thenReturn(Optional.of(officialLectureFirst));
      when(enrollmentRepository.findById(any())).thenReturn(Optional.empty());
      when(lectureRepository.getNumberOfConflictLectures(any(), any())).thenReturn(0L);

      enrollmentService.addOfficialLectureToTimetable(userId, schoolId, timetableId, enrollmentCreateRequestFirst);

      verify(enrollmentRepository).save(any());
    }
  }

  @Nested
  @DisplayName("addCustomLectureToTimetable() 테스트")
  class AddCustomLectureToTimetableTest {
    private CustomLectureDetail customLectureDetail = CustomLectureDetail.builder()
        .day(MON)
        .startTime("10:00")
        .endTime("10:50")
        .build();

    private CustomLectureRequest customLectureRequest = CustomLectureRequest.builder()
        .name("custom 강의")
        .lectureDetails(new ArrayList<>(Arrays.asList(customLectureDetail)))
        .build();

    @Test
    @DisplayName("시간표에 강의가 추가될 수 있는 경우 테스트")
    void testEnrollmentSave() {
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenReturn(Optional.of(timetable));
      when(lectureRepository.getNumberOfConflictLectures(any(), any())).thenReturn(0L);
      when(lectureRepository.save(any())).thenReturn(customLecture);

      enrollmentService.addCustomLectureToTimetable(userId, timetableId, customLectureRequest);

      verify(lectureRepository).save(any());
      verify(lectureDetailRepository).save(any());
      verify(enrollmentRepository).save(any());
    }
  }

  @Nested
  @DisplayName("updateCustomLecture() 테스트")
  class UpdateCustomLectureTest {
    private CustomLectureDetail customLectureDetail = CustomLectureDetail.builder()
        .day(MON)
        .startTime("10:00")
        .endTime("10:50")
        .build();

    private CustomLectureRequest customLectureRequest = CustomLectureRequest.builder()
        .name("custom 강의")
        .lectureDetails(new ArrayList<>(Arrays.asList(customLectureDetail)))
        .build();

    @Test
    @DisplayName("시간표에 등록된 custom lecture 수정 테스트")
    void testUpdateCustomLecture() {
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenReturn(Optional.of(timetable));
      when(lectureRepository.findById(customLectureId)).thenReturn(Optional.of(customLecture));
      when(lectureRepository.getNumberOfConflictLectures(any(), any())).thenReturn(0L);

      enrollmentService.updateCustomLecture(userId, timetableId, customLectureId, customLectureRequest);

      verify(lectureDetailRepository).deleteCustomLectureDetailsByLectureId(customLecture.getId());
      verify(lectureDetailRepository).save(any());
    }
  }

  @Nested
  @DisplayName("deleteLectureFromTimetable() 테스트")
  class DeleteLectureFromTimetableTest {
    @Test
    @DisplayName("official 강의 시간표에서 삭제 테스트")
    void testDeleteOfficialLectureFromTimetable() {
      Enrollment enrollment = new Enrollment(officialLectureFirst, timetable);
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenReturn(Optional.of(timetable));
      when(enrollmentRepository.findById(any())).thenReturn(Optional.of(enrollment));
      when(lectureRepository.isCustomLecture(idOfOfficialLectureFirst)).thenReturn(false);

      enrollmentService.deleteLectureFromTimetable(userId, timetableId, idOfOfficialLectureFirst);

      verify(enrollmentRepository).delete(enrollment);
      verify(lectureDetailRepository, never()).deleteCustomLectureDetailsByLectureId(idOfOfficialLectureFirst);
      verify(lectureRepository, never()).deleteById(idOfOfficialLectureFirst);
    }
    
     @Test
     @DisplayName("custom 강의 시간표에서 삭제 테스트")
     void testDeleteCustomLectureFromTimetable() {
       Enrollment enrollment = new Enrollment(officialLectureFirst, timetable);
       when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenReturn(Optional.of(timetable));
       when(enrollmentRepository.findById(any())).thenReturn(Optional.of(enrollment));
       when(lectureRepository.isCustomLecture(idOfOfficialLectureFirst)).thenReturn(true);

       enrollmentService.deleteLectureFromTimetable(userId, timetableId, idOfOfficialLectureFirst);

       verify(enrollmentRepository).delete(enrollment);
       verify(lectureDetailRepository).deleteCustomLectureDetailsByLectureId(idOfOfficialLectureFirst);
       verify(lectureRepository).deleteById(idOfOfficialLectureFirst);
     }
  }

}