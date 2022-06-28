package com.prgrms.coretime.timetable.domain.repository.enrollment;

import com.prgrms.coretime.timetable.domain.enrollment.Enrollment;
import com.prgrms.coretime.timetable.domain.enrollment.EnrollmentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId>, EnrollmentCustomRepository {

}
