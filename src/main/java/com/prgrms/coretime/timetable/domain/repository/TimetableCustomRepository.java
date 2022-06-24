package com.prgrms.coretime.timetable.domain.repository;

import com.prgrms.coretime.timetable.domain.Semester;

public interface TimetableCustomRepository {
  long countDuplicateTimetableName(String name, Integer year, Semester semester);
}
