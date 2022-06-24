package com.prgrms.coretime.timetable.domain.repository;

import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimetableRepository extends JpaRepository<Timetable, Long>, TimetableCustomRepository {

}
