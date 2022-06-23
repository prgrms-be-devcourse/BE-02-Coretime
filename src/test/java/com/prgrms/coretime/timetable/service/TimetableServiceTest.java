package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.timetable.domain.Semester.SECOND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.repository.TemporaryUserRepository;
import com.prgrms.coretime.timetable.domain.repository.TimetableRepository;
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
      .semester(SECOND)
      .year(2022)
      .build();

  User user = new LocalUser();

  @Nested
  @DisplayName("createTimetable() 테스트")
  class TimetableTableCreationTest {
    @Test
    @DisplayName("사용자가 존재하지 않는 경우 시간표 생성 테스트")
    void testCreateTimetableException() {
      when(userRepository.findById(any())).thenThrow(new IllegalArgumentException());

      try {
        timetableService.createTimetable(timetableCreateRequest);
      }catch (Exception e) {
        verify(timetableRepository, never()).save(any());
      }
    }

    @Test
    @DisplayName("사용자가 존재하는 경우 시간표 생성 테스트")
    void testCreateTimetable() {
      when(userRepository.findById(any())).thenReturn(Optional.of(user));

      timetableService.createTimetable(timetableCreateRequest);

      verify(timetableRepository).save(any());
    }
  }

}