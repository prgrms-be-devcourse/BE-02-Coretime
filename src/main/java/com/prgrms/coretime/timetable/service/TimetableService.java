
package com.prgrms.coretime.timetable.service;

import com.prgrms.coretime.common.error.NotFoundException;
import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.repository.TemporaryUserRepository;
import com.prgrms.coretime.timetable.domain.repository.timetable.TimetableRepository;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import com.prgrms.coretime.timetable.dto.request.TimetableCreateRequest;
import com.prgrms.coretime.timetable.dto.request.TimetableUpdateRequest;
import com.prgrms.coretime.timetable.dto.response.TimetableInfo;
import com.prgrms.coretime.timetable.dto.response.TimetableResponse;
import com.prgrms.coretime.timetable.dto.response.TimetablesResponse;
import com.prgrms.coretime.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class TimetableService {
  private final TimetableRepository timetableRepository;

  // TODO : 머지 후 TemporaryUserRepository -> UserRepository로 교체
  private final TemporaryUserRepository userRepository;

  @Transactional
  public Long createTimetable(@RequestBody @Valid TimetableCreateRequest timetableCreateRequest) {
    // TODO : 사용자 ID 가져오는 로직 추가
    Long userId = 1L;
    User user  = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다!"));

    long duplicateNameCount = timetableRepository.countDuplicateTimetableName(timetableCreateRequest.getName().trim(), timetableCreateRequest.getYear(), timetableCreateRequest.getSemester());
    if(duplicateNameCount != 0) {
      throw new IllegalArgumentException("이미 사용중인 이름입니다.");
    }

    Timetable newTimetable = Timetable.builder()
        .name(timetableCreateRequest.getName().trim())
        .year(timetableCreateRequest.getYear())
        .semester(timetableCreateRequest.getSemester())
        .build();
    newTimetable.setUser(user);

    Timetable createdTimetable = timetableRepository.save(newTimetable);
    return createdTimetable.getId();
  }

  @Transactional(readOnly = true)
  public TimetablesResponse getTimetables(Integer year, Semester semester) {
    List<TimetableInfo> timetables = timetableRepository.getTimetables(year, semester).stream()
        .map(timetable -> new TimetableInfo(timetable.getId(), timetable.getName()))
        .collect(Collectors.toList());

    return new TimetablesResponse(timetables);
  }

  @Transactional(readOnly = true)
  public TimetableResponse getTimetable(Long timetableId) {
    Timetable timetable = timetableRepository.findById(timetableId).orElseThrow(() -> new NotFoundException("시간표를 찾을 수 업습니다."));

    // TODO : lecture와 lecture DETAIL에 대한 정보 조회와 조회한 값 DTO에 전달
    // TODO : 추가된 내용에 따른 테스트 코드 작성

    return TimetableResponse.builder()
        .timetableId(timetable.getId())
        .name(timetable.getName())
        .year(timetable.getYear())
        .semester(timetable.getSemester())
        .build();
  }

  @Transactional
  public void updateTimetableName(Long timetableId, TimetableUpdateRequest timetableUpdateRequest) {
    Timetable timetable = timetableRepository.findById(timetableId).orElseThrow(() -> new NotFoundException("시간표를 찾을 수 업습니다."));
    timetable.updateName(timetableUpdateRequest.getName().trim());
  }

  @Transactional
  public void deleteTimetable(Long timetableId) {
    Timetable timetable = timetableRepository.findById(timetableId).orElseThrow(() -> new NotFoundException("시간표를 찾을 수 업습니다."));

    // TODO : enrollment에서 삭제(timetableId에 해당하는 항목 삭제)
    // TODO : CUSTOM 강의 삭제

    timetableRepository.delete(timetable);
  }
}
