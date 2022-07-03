package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.common.ErrorCode.NOT_FOUND;
import static com.prgrms.coretime.common.ErrorCode.USER_NOT_FOUND;
import static com.prgrms.coretime.timetable.domain.Semester.FIRST;
import static com.prgrms.coretime.timetable.domain.Semester.SECOND;
import static com.prgrms.coretime.timetable.domain.Semester.SUMMER;
import static com.prgrms.coretime.timetable.domain.Semester.WINTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.coretime.common.error.exception.DuplicateRequestException;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.friend.domain.FriendRepository;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.repository.timetable.TimetableRepository;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import com.prgrms.coretime.timetable.dto.request.TimetableCreateRequest;
import com.prgrms.coretime.timetable.dto.response.FriendDefaultTimetableInfo;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
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
class TimetableServiceTest {
  @Mock
  private TimetableRepository timetableRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private FriendRepository friendRepository;
  @InjectMocks
  private TimetableService timetableService;

  private User user = new User("a@school.com", "testerA");
  private Timetable timetable = Timetable.builder()
      .name("시간표1")
      .year(2022)
      .semester(SECOND)
      .user(user)
      .isDefault(true)
      .build();

  @Nested
  @DisplayName("createTimetable() 테스트")
  class TimetableTableCreationTest {
    private TimetableCreateRequest timetableCreateRequest;

    @BeforeEach
    void setUp() {
      timetableCreateRequest = TimetableCreateRequest.builder()
          .name("시간표1")
          .year(2022)
          .semester(SECOND)
          .build();
    }

    @Test
    @DisplayName("사용자가 존재하지 않는 경우 시간표 생성 테스트")
    void testCreateTimetableNotFoundException() {
      when(userRepository.findById(any())).thenThrow(new NotFoundException(USER_NOT_FOUND));

      try {
        timetableService.createTimetable(1L, timetableCreateRequest);
      }catch (Exception e) {
        verify(timetableRepository, never()).getTimetableBySameName(any(), any(), any(), any());
        verify(timetableRepository, never()).save(any());
      }
    }

    @Test
    @DisplayName("시간표 이름이 중복되는 경우 테스트")
    void testCreateTimetableNameDuplicate() {
      when(userRepository.findById(any())).thenReturn(Optional.of(user));
      when(timetableRepository.getTimetableBySameName(any(), any(), any(), any())).thenReturn(Optional.of(timetable));

      try {
        timetableService.createTimetable(1L, timetableCreateRequest);
      }catch (DuplicateRequestException e) {
        verify(timetableRepository, never()).save(any());
      }
    }

    @Test
    @DisplayName("정상적으로 시간표를 생성하는 경우 테스트")
    void testCreateTimetable() {
      when(userRepository.findById(any())).thenReturn(Optional.of(user));
      when(timetableRepository.getTimetableBySameName(any(), any(), any(), any())).thenReturn(Optional.empty());
      when(timetableRepository.save(any())).thenReturn(timetable);

      timetableService.createTimetable(1L, timetableCreateRequest);

      verify(timetableRepository).save(any());
    }
  }

  @Test
  @DisplayName("DefaultTimetableInfo들이 연도와 학기 내림차순으로 잘 정렬되는지 확인하는 테스트")
  void testSortingOfDefaultTimetableInfos() {
    List<Timetable> timetables = new ArrayList<>();

    for(Semester semester : Semester.values()) {
      timetables.add(Timetable.builder()
          .name("시간표"+semester.getOrder())
          .year(2022)
          .semester(semester)
          .user(user)
          .isDefault(true)
          .build());
      timetables.add(Timetable.builder()
          .name("시간표"+semester.getOrder())
          .year(2021)
          .semester(semester)
          .user(user)
          .isDefault(true)
          .build());
    }

    Long userId = 1L;
    Long friendId = 2L;
    when(friendRepository.existsFriendRelationship(userId, friendId)).thenReturn(true);
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