package com.prgrms.coretime.timetable.domain.repository.enrollment;

import com.prgrms.coretime.timetable.domain.enrollment.Enrollment;
import java.util.List;

public interface EnrollmentCustomRepository {
  List<Enrollment> getEnrollmentsWithLectureByTimetableId(Long timetableId, LectureType lectureType);

  void deleteByTimetableId(Long timetableId);
}
