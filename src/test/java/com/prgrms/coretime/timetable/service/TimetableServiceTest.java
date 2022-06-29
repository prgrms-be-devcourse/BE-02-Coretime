package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.common.ErrorCode.NOT_FOUND;
import static com.prgrms.coretime.timetable.domain.Semester.SECOND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.coretime.common.error.NotFoundException;
import com.prgrms.coretime.timetable.domain.repository.TemporaryUserRepository;
import com.prgrms.coretime.timetable.domain.repository.timetable.TimetableRepository;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import com.prgrms.coretime.timetable.dto.request.TimetableCreateRequest;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
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
  TimetableRepository timetableRepository;

  // TODO : 머지 후 TemporaryUserRepository -> UserRepository로 교체
  @Mock
  TemporaryUserRepository userRepository;

  @InjectMocks
  TimetableService timetableService;

  TimetableCreateRequest timetableCreateRequest = TimetableCreateRequest.builder()
      .name("시간표1")
      .year(2022)
      .semester(SECOND)
      .build();

  User user = new LocalUser();

  Timetable timetable = Timetable.builder()
      .name("시간표1")
      .year(2022)
      .semester(SECOND)
      .build();


  @Nested
  @DisplayName("createTimetable() 테스트")
  class TimetableTableCreationTest {
    @Test
    @DisplayName("사용자가 존재하지 않는 경우 시간표 생성 테스트")
    void testCreateTimetableNotFoundException() {
      when(userRepository.findById(any())).thenThrow(new NotFoundException(NOT_FOUND));

      try {
        timetableService.createTimetable(timetableCreateRequest);
      }catch (Exception e) {
        verify(timetableRepository, never()).countDuplicateTimetableName(timetableCreateRequest.getName(), timetableCreateRequest.getYear(), timetableCreateRequest.getSemester());
        verify(timetableRepository, never()).save(any());
      }
    }

    @Test
    @DisplayName("시간표 이름이 중복되는 경우 테스트")
    void testCreateTimetableNameDuplicate() {
      when(userRepository.findById(any())).thenReturn(Optional.of(user));
      when(timetableRepository.countDuplicateTimetableName(timetableCreateRequest.getName(), timetableCreateRequest.getYear(), timetableCreateRequest.getSemester())).thenReturn(1L);

      try {
        timetableService.createTimetable(timetableCreateRequest);
      }catch (Exception e) {
        verify(timetableRepository, never()).save(any());
      }
    }

    @Test
    @DisplayName("사용자도 존재하고 시간표 이름도 중복되지 않는 경우 시간표 생성 테스트")
    void testCreateTimetable() {
      when(userRepository.findById(any())).thenReturn(Optional.of(user));
      when(timetableRepository.save(any())).thenReturn(timetable);

      timetableService.createTimetable(timetableCreateRequest);

      verify(timetableRepository).save(any());
    }
  }
}