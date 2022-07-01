package com.prgrms.coretime.timetable.domain.repository.timetable;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import java.util.List;
import java.util.Optional;

public interface TimetableCustomRepository {
  Optional<Timetable> getTimetableBySameName(Long userId, String name, Integer year, Semester semester);

  Optional<Timetable> getDefaultTimetable(Long userId, Integer year, Semester semester);

  Optional<Timetable> getTimetableByUserIdAndTimetableId(Long userId, Long timetableId);

  Optional<Timetable> getRecentlyAddedTimetable(Long userId, Integer year, Semester semester);

  List<Timetable> getTimetables(Long userId, Integer year, Semester semester);

  boolean isFirstTimetable(Long userId, Integer year, Semester semester);

  void deleteByTimetableId(Long timetableId);
}
