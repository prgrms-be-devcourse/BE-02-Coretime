package com.prgrms.coretime.timetable.domain.repository.timetable;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import java.util.List;

public interface TimetableCustomRepository {
  boolean isDuplicateTimetableName(Long userId, String name, Integer year, Semester semester);

  List<Timetable> getTimetables(Long userId, Integer year, Semester semester);
}
