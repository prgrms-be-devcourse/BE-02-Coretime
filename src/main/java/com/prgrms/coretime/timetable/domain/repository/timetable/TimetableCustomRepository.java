package com.prgrms.coretime.timetable.domain.repository.timetable;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import java.util.List;
import java.util.Optional;

public interface TimetableCustomRepository {
  boolean isDuplicateTimetableName(Long userId, String name, Integer year, Semester semester);

  List<Timetable> getTimetables(Long userId, Integer year, Semester semester);

  Optional<Timetable> getTimetableByUserIdAndTimetableId(Long userId, Long timetableId);

  void deleteByTimetableId(Long timetableId);
}
