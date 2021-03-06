package com.prgrms.coretime.timetable.domain.repository.timetable;

import com.prgrms.coretime.timetable.domain.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimetableRepository extends JpaRepository<Timetable, Long>, TimetableCustomRepository {

}
