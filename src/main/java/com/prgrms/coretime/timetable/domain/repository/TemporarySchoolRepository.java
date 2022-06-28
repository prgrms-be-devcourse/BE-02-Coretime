package com.prgrms.coretime.timetable.domain.repository;

import com.prgrms.coretime.school.domain.School;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemporarySchoolRepository extends JpaRepository<School, Long> {

}
