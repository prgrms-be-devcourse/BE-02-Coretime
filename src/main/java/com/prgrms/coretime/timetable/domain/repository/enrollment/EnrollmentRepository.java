package com.prgrms.coretime.timetable.domain.repository.enrollment;

import com.prgrms.coretime.timetable.domain.Enrollment;
import com.prgrms.coretime.timetable.domain.EnrollmentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId>,
    EnrollmentCustomRepository {

}
