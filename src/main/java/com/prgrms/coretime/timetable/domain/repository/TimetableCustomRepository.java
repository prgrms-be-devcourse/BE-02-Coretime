package com.prgrms.coretime.timetable.domain.repository;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import java.util.List;

public interface TimetableCustomRepository {
  long countDuplicateTimetableName(String name, Integer year, Semester semester);

  List<Timetable> getTimetables(Integer year, Semester semester);
}
