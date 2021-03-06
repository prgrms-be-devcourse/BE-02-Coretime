package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.common.ErrorCode.DUPLICATE_TIMETABLE_NAME;
import static com.prgrms.coretime.common.ErrorCode.NOT_FRIEND;
import static com.prgrms.coretime.common.ErrorCode.TIMETABLE_NOT_FOUND;
import static com.prgrms.coretime.common.ErrorCode.USER_NOT_FOUND;
import static com.prgrms.coretime.timetable.domain.Semester.FIRST;
import static com.prgrms.coretime.timetable.domain.Semester.SECOND;
import static com.prgrms.coretime.timetable.domain.Semester.SUMMER;
import static com.prgrms.coretime.timetable.domain.Semester.WINTER;
import static com.prgrms.coretime.timetable.domain.repository.enrollment.LectureType.ALL;
import static com.prgrms.coretime.timetable.domain.repository.enrollment.LectureType.CUSTOM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.coretime.common.error.exception.DuplicateRequestException;
import com.prgrms.coretime.common.error.exception.InvalidRequestException;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.friend.domain.FriendRepository;
import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.Enrollment;
import com.prgrms.coretime.timetable.domain.repository.enrollment.EnrollmentRepository;
import com.prgrms.coretime.timetable.domain.repository.lecture.LectureRepository;
import com.prgrms.coretime.timetable.domain.repository.lectureDetail.LectureDetailRepository;
import com.prgrms.coretime.timetable.domain.repository.timetable.TimetableRepository;
import com.prgrms.coretime.timetable.domain.Timetable;
import com.prgrms.coretime.timetable.dto.request.TimetableCreateRequest;
import com.prgrms.coretime.timetable.dto.request.TimetableUpdateRequest;
import com.prgrms.coretime.timetable.dto.response.FriendDefaultTimetableInfo;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TimetableServiceTest {
  @Mock
  private TimetableRepository timetableRepository;
  @Mock
  private TimetableValidator timetableValidator;
  @Mock
  private UserRepository userRepository;
  @Mock
  private FriendRepository friendRepository;
  @Mock
  private EnrollmentRepository enrollmentRepository;
  @Mock
  private LectureRepository lectureRepository;
  @Mock
  private LectureDetailRepository lectureDetailRepository;
  @InjectMocks
  private TimetableService timetableService;

  private Long userId = 1L;
  private User user = new User("a@school.com", "testerA");
  private Long timetableId = 2L;
  private Timetable timetable = Timetable.builder()
      .name("?????????1")
      .year(2022)
      .semester(FIRST)
      .user(user)
      .isDefault(true)
      .build();
  private Long friendId = 3L;

  @Nested
  @DisplayName("createTimetable() ?????????")
  class CreateTimetableTest {
    private TimetableCreateRequest timetableCreateRequest = TimetableCreateRequest.builder()
        .name("?????????1")
        .year(2022)
        .semester(FIRST)
        .build();

    @Test
    @DisplayName("???????????? ???????????? ?????? ?????? ?????????")
    void testUserNotFound() {
      when(userRepository.findById(userId)).thenThrow(new NotFoundException(USER_NOT_FOUND));

      try {
        timetableService.createTimetable(userId, timetableCreateRequest);
      }catch (Exception e) {
        verify(timetableValidator, never()).validateSameNameWhenCreate(any(), any(), any(), any());
        verify(timetableRepository, never()).save(any());
      }
    }

    @Test
    @DisplayName("????????? ????????? ???????????? ?????? ?????????")
    void testDuplicateTimetableName() {
      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      doThrow(new DuplicateRequestException(DUPLICATE_TIMETABLE_NAME)).when(timetableValidator).validateSameNameWhenCreate(userId, timetableCreateRequest.getName(), timetableCreateRequest.getYear(), timetableCreateRequest.getSemester());

      try {
        timetableService.createTimetable(userId, timetableCreateRequest);
      }catch (DuplicateRequestException e) {
        verify(timetableRepository, never()).save(any());
      }
    }

    @Test
    @DisplayName("??????????????? ???????????? ???????????? ?????? ?????????")
    void testCreateTimetable() {
      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      when(timetableRepository.save(any())).thenReturn(timetable);

      timetableService.createTimetable(userId, timetableCreateRequest);

      verify(timetableRepository).save(any());
    }
  }

  @Nested
  @DisplayName("getDefaultTimetable() ?????????")
  class GetDefaultTimetableTest {
    @Test
    @DisplayName("?????? ???????????? ???????????? ????????? ?????? ?????????")
    void testDefaultTimetableNotFoundException() {
      when(timetableRepository.getDefaultTimetable(userId, 2022, FIRST)).thenThrow(new NotFoundException(TIMETABLE_NOT_FOUND));

      try {
        timetableService.getDefaultTimetable(userId, 2022, FIRST);
      } catch (NotFoundException e) {
        verify(enrollmentRepository, never()).getEnrollmentsWithLectureByTimetableId(any(), any());
      }
    }

    @Test
    @DisplayName("?????? ???????????? ????????? ??? ?????? ?????? ?????????")
    void testGetDefaultTimetable() {
      when(timetableRepository.getDefaultTimetable(userId, 2022, FIRST)).thenReturn(Optional.of(timetable));
      when(enrollmentRepository.getEnrollmentsWithLectureByTimetableId(timetable.getId(), ALL)).thenReturn(new ArrayList<Enrollment>());

      timetableService.getDefaultTimetable(userId, 2022, FIRST);

      verify(enrollmentRepository).getEnrollmentsWithLectureByTimetableId(timetable.getId(), ALL);
    }
  }

  @Nested
  @DisplayName("getTimetable() ?????????")
  class GetTimetableTest {
    @Test
    @DisplayName("????????? ???????????? ????????? ??? ?????? ?????? ?????????")
    void testTimetableNotFoundException() {
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenThrow(new NotFoundException(TIMETABLE_NOT_FOUND));

      try {
        timetableService.getTimetable(userId, timetableId);
      }catch (NotFoundException e) {
        verify(enrollmentRepository, never()).getEnrollmentsWithLectureByTimetableId(any(), any());
      }
    }

    @Test
    @DisplayName("????????? ???????????? ????????? ??? ?????? ?????? ?????????")
    void testGetTimetable() {
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenReturn(Optional.of(timetable));
      when(enrollmentRepository.getEnrollmentsWithLectureByTimetableId(timetable.getId(), ALL)).thenReturn(new ArrayList<Enrollment>());

      timetableService.getTimetable(userId, timetableId);

      verify(enrollmentRepository).getEnrollmentsWithLectureByTimetableId(timetable.getId(), ALL);
    }
  }

  @Nested
  @DisplayName("getFriendDefaultTimetableInfos() ?????????")
  class GetFriendDefaultTimetableInfosTest {
    @Test
    @DisplayName("?????? ????????? ?????? ?????? ?????????")
    void testNotFriendException() {
      doThrow(new InvalidRequestException(NOT_FRIEND)).when(timetableValidator).validateFriendRelationship(userId, friendId);

      try {
        timetableService.getFriendDefaultTimetableInfos(userId, friendId);
      }catch (InvalidRequestException e) {
        verify(timetableRepository, never()).getDefaultTimetables(friendId);
      }
    }

    @Test
    @DisplayName("DefaultTimetableInfo?????? ????????? ?????? ?????????????????? ??? ??????????????? ???????????? ?????????")
    void testSortingOfDefaultTimetableInfos() {
      List<Timetable> timetables = new ArrayList<>();

      for(Semester semester : Semester.values()) {
        timetables.add(Timetable.builder()
            .name("?????????"+semester.getOrder())
            .year(2022)
            .semester(semester)
            .user(user)
            .isDefault(true)
            .build());
        timetables.add(Timetable.builder()
            .name("?????????"+semester.getOrder())
            .year(2021)
            .semester(semester)
            .user(user)
            .isDefault(true)
            .build());
      }

      when(timetableRepository.getDefaultTimetables(friendId)).thenReturn(timetables);

      List<FriendDefaultTimetableInfo> friendDefaultTimetableInfos = timetableService.getFriendDefaultTimetableInfos(userId, friendId);

      assertThat(friendDefaultTimetableInfos.get(0).getYear()).isEqualTo(2022);
      assertThat(friendDefaultTimetableInfos.get(0).getSemester()).isEqualTo(WINTER);

      assertThat(friendDefaultTimetableInfos.get(1).getYear()).isEqualTo(2022);
      assertThat(friendDefaultTimetableInfos.get(1).getSemester()).isEqualTo(SECOND);

      assertThat(friendDefaultTimetableInfos.get(2).getYear()).isEqualTo(2022);
      assertThat(friendDefaultTimetableInfos.get(2).getSemester()).isEqualTo(SUMMER);

      assertThat(friendDefaultTimetableInfos.get(3).getYear()).isEqualTo(2022);
      assertThat(friendDefaultTimetableInfos.get(3).getSemester()).isEqualTo(FIRST);

      assertThat(friendDefaultTimetableInfos.get(4).getYear()).isEqualTo(2021);
      assertThat(friendDefaultTimetableInfos.get(4).getSemester()).isEqualTo(WINTER);

      assertThat(friendDefaultTimetableInfos.get(5).getYear()).isEqualTo(2021);
      assertThat(friendDefaultTimetableInfos.get(5).getSemester()).isEqualTo(SECOND);

      assertThat(friendDefaultTimetableInfos.get(6).getYear()).isEqualTo(2021);
      assertThat(friendDefaultTimetableInfos.get(6).getSemester()).isEqualTo(SUMMER);

      assertThat(friendDefaultTimetableInfos.get(7).getYear()).isEqualTo(2021);
      assertThat(friendDefaultTimetableInfos.get(7).getSemester()).isEqualTo(FIRST);
    }
  }

  @Nested
  @DisplayName("getDefaultTimetableOfFriend() ?????????")
  class GetDefaultTimetableOfFriendTest {
    @Test
    @DisplayName("?????? ????????? ?????? ?????? ?????????")
    void testNotFriend() {
      doThrow(new InvalidRequestException(NOT_FRIEND)).when(timetableValidator).validateFriendRelationship(userId, friendId);

      try {
        timetableService.getDefaultTimetableOfFriend(userId, friendId, 2022, FIRST);
      } catch (InvalidRequestException e) {
        verify(timetableRepository, never()).getDefaultTimetable(any(), any(), any());
        verify(enrollmentRepository, never()).getEnrollmentsWithLectureByTimetableId(any(), any());
      }
    }

    @Test
    @DisplayName("????????? ?????? ???????????? ???????????? ?????? ?????? ?????????")
    void testTimetableNotFound() {
      when(timetableRepository.getDefaultTimetable(userId, 2022, FIRST)).thenThrow(new NotFoundException(TIMETABLE_NOT_FOUND));

      try {
        timetableService.getDefaultTimetableOfFriend(userId, friendId, 2022, FIRST);
      } catch (NotFoundException e) {
        verify(timetableRepository).getDefaultTimetable(userId, 2022, FIRST);
        verify(enrollmentRepository, never()).getEnrollmentsWithLectureByTimetableId(timetable.getId(), ALL);
      }
    }

    @Test
    @DisplayName("????????? ?????? ???????????? ????????? ??? ?????? ?????? ?????????")
    void testGetDefaultTimetableOfFriend() {
      when(timetableRepository.getDefaultTimetable(userId, 2022, FIRST)).thenReturn(Optional.of(timetable));
      when(enrollmentRepository.getEnrollmentsWithLectureByTimetableId(timetable.getId(), ALL)).thenReturn(new ArrayList<Enrollment>());

      timetableService.getDefaultTimetableOfFriend(userId, friendId, 2022, FIRST);

      verify(timetableRepository).getDefaultTimetable(userId, 2022, FIRST);
      verify(enrollmentRepository).getEnrollmentsWithLectureByTimetableId(timetable.getId(), ALL);
    }
  }

  @Nested
  @DisplayName("updateTimetable() ?????????")
  class UpdateTimetableTest {
    private TimetableUpdateRequest timetableUpdateRequest = new TimetableUpdateRequest("?????????1", false);
    private TimetableUpdateRequest timetableDefaultTableUpdateRequest= new TimetableUpdateRequest("?????????2", true);
    private Timetable sameNameTable = Timetable.builder()
        .name("?????????1")
        .year(2022)
        .semester(FIRST)
        .user(user)
        .isDefault(false)
        .build();
    private Timetable defaultTimetable = Timetable.builder()
        .name("?????????2")
        .year(2022)
        .semester(FIRST)
        .user(user)
        .isDefault(true)
        .build();

    @Test
    @DisplayName("???????????? ???????????? ?????? ?????? ?????????")
    void testTimetableNotFound() {
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenThrow(new NotFoundException(TIMETABLE_NOT_FOUND));

      try {
        timetableService.updateTimetable(userId, timetableId, timetableUpdateRequest);
      }catch (NotFoundException e) {
        verify(timetableValidator, never()).validateSameNameWhenUpdate(any(), any(), any(), any(), any());
        verify(timetableRepository, never()).getDefaultTimetable(any(), any(), any());
      }
    }

    @Test
    @DisplayName("????????? ????????? ???????????? ???????????? ??????")
    void testDuplicateTimetableName() {
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenReturn(Optional.of(timetable));
      doThrow(new DuplicateRequestException(DUPLICATE_TIMETABLE_NAME)).when(timetableValidator).validateSameNameWhenUpdate(any(), any(), any(), any(), any());

      try {
        timetableService.updateTimetable(userId, timetableId, timetableUpdateRequest);
      }catch (DuplicateRequestException e) {
        verify(timetableRepository, never()).getDefaultTimetable(any(), any(), any());
      }
    }

    @Test
    @DisplayName("default ????????? ???????????? ??????")
    void testUpdateDefaultTable() {
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenReturn(Optional.of(timetable));
      when(timetableRepository.getDefaultTimetable(userId, timetable.getYear(), timetable.getSemester())).thenReturn(Optional.of(defaultTimetable));

      timetableService.updateTimetable(userId, timetableId, timetableDefaultTableUpdateRequest);

      verify(timetableRepository).getDefaultTimetable(userId, timetable.getYear(), timetable.getSemester());
    }
  }

  @Nested
  @DisplayName("deleteTimetable() ?????????")
  class DeleteTimetableTest {
    private Timetable notDefaultTable = Timetable.builder()
        .name("?????????1")
        .year(2022)
        .semester(FIRST)
        .user(user)
        .isDefault(false)
        .build();

    @Test
    @DisplayName("???????????? ???????????? ?????? ?????? ?????????")
    void testTimetableNotFound() {
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenThrow(new NotFoundException(TIMETABLE_NOT_FOUND));

      try {
        timetableService.deleteTimetable(userId, timetableId);
      }catch (NotFoundException e) {
        verify(enrollmentRepository, never()).getEnrollmentsWithLectureByTimetableId(timetable.getId(), CUSTOM);
        verify(enrollmentRepository, never()).deleteByTimetableId(timetable.getId());
        verify(lectureDetailRepository, never()).deleteLectureDetailsByLectureIds(any());
        verify(lectureRepository, never()).deleteLectureByLectureIds(any());
        verify(timetableRepository, never()).deleteByTimetableId(timetable.getId());
      }
    }

    @Test
    @DisplayName("????????? ???????????? ?????? ???????????? ?????? ?????? ?????????")
    void testDeleteNotDefaultTimetable() {
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenReturn(Optional.of(notDefaultTable));
      when(enrollmentRepository.getEnrollmentsWithLectureByTimetableId(notDefaultTable.getId(), CUSTOM)).thenReturn(new ArrayList<Enrollment>());

      timetableService.deleteTimetable(userId, timetableId);

      verify(enrollmentRepository).deleteByTimetableId(notDefaultTable.getId());
      verify(lectureDetailRepository).deleteLectureDetailsByLectureIds(any());
      verify(lectureRepository).deleteLectureByLectureIds(any());
      verify(timetableRepository).delete(notDefaultTable);
      verify(timetableRepository, never()).getRecentlyAddedTimetable(userId, notDefaultTable.getYear(), notDefaultTable.getSemester());
    }

    @Test
    @DisplayName("????????? ???????????? ?????? ???????????? ?????? ?????????")
    void testDeleteDefaultTimetable() {
      when(timetableRepository.getTimetableByUserIdAndTimetableId(userId, timetableId)).thenReturn(Optional.of(timetable));
      when(enrollmentRepository.getEnrollmentsWithLectureByTimetableId(timetable.getId(), CUSTOM)).thenReturn(new ArrayList<Enrollment>());

      timetableService.deleteTimetable(userId, timetableId);

      verify(enrollmentRepository).deleteByTimetableId(timetable.getId());
      verify(lectureDetailRepository).deleteLectureDetailsByLectureIds(any());
      verify(lectureRepository).deleteLectureByLectureIds(any());
      verify(timetableRepository).delete(timetable);
      verify(timetableRepository).getRecentlyAddedTimetable(userId, timetable.getYear(), timetable.getSemester());
    }
  }
}