package com.prgrms.coretime.timetable.domain.repository.enrollment;

import com.prgrms.coretime.timetable.domain.enrollment.Enrollment;
import java.util.List;

public interface EnrollmentCustomRepository {
  List<Enrollment> getEnrollmentByIdWithLecture(Long timetableId);
}
