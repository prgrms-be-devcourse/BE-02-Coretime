package com.prgrms.coretime.timetable.domain.repository.lectureDetail;

import com.prgrms.coretime.timetable.domain.LectureDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureDetailRepository extends JpaRepository<LectureDetail, Long>, LectureDetailCustomRepository {

}
