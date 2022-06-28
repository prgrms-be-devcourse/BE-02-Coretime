package com.prgrms.coretime.timetable.domain.repository.lecture;

import com.prgrms.coretime.timetable.domain.lecture.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long>, LectureCustomRepository {

}
