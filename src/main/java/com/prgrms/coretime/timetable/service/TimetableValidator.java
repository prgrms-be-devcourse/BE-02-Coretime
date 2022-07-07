package com.prgrms.coretime.timetable.service;

import static com.prgrms.coretime.common.ErrorCode.DUPLICATE_TIMETABLE_NAME;
import static com.prgrms.coretime.common.ErrorCode.NOT_FRIEND;

import com.prgrms.coretime.common.error.exception.DuplicateRequestException;
import com.prgrms.coretime.common.error.exception.InvalidRequestException;
import com.prgrms.coretime.friend.domain.FriendRepository;
import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.Timetable;
import com.prgrms.coretime.timetable.domain.repository.timetable.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TimetableValidator {
  private final TimetableRepository timetableRepository;
  private final FriendRepository friendRepository;

  @Transactional(readOnly = true)
  public void validateSameNameWhenCreate(Long userId, String timetableName, Integer year, Semester semester) {
    if(timetableRepository.getTimetableBySameName(userId, timetableName, year, semester).isPresent()) {
      throw new DuplicateRequestException(DUPLICATE_TIMETABLE_NAME);
    }
  }

  @Transactional(readOnly = true)
  public void validateSameNameWhenUpdate(Long userId, String timetableName, Integer year, Semester semester, Timetable timetable) {
    Timetable sameNameTable = timetableRepository.getTimetableBySameName(userId, timetableName, year, semester).orElse(timetable);
    if(timetable != sameNameTable) {
      throw new DuplicateRequestException(DUPLICATE_TIMETABLE_NAME);
    }
  }

  @Transactional(readOnly = true)
  public void validateFriendRelationship(Long userId, Long friendId) {
    if(!friendRepository.existsFriendRelationship(userId, friendId)) {
      throw new InvalidRequestException(NOT_FRIEND);
    }
  }
}
