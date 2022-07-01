package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.common.ErrorCode.NOT_FOUND;
import static com.prgrms.coretime.timetable.domain.Semester.SECOND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.timetable.domain.repository.timetable.TimetableRepository;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import com.prgrms.coretime.timetable.dto.request.TimetableCreateRequest;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
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
  private UserRepository userRepository;

  @InjectMocks
  private TimetableService timetableService;

  private TimetableCreateRequest timetableCreateRequest = TimetableCreateRequest.builder()
      .name("시간표1")
      .year(2022)
      .semester(SECOND)
      .build();

  private User user = LocalUser
      .builder()
      .build();

  private Timetable timetable = Timetable.builder()
      .name("시간표1")
      .year(2022)
      .semester(SECOND)
      .user(user)
      .isDefault(false)
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
        verify(timetableRepository, never()).isDuplicateTimetableName(any(), any(), any(), any());
        verify(timetableRepository, never()).save(any());
      }
    }

    @Test
    @DisplayName("시간표 이름이 중복되는 경우 테스트")
    void testCreateTimetableNameDuplicate() {
       when(userRepository.findById(any())).thenReturn(Optional.of(user));
       when(timetableRepository.isDuplicateTimetableName(any(), any(), any(), any())).thenReturn(true);

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