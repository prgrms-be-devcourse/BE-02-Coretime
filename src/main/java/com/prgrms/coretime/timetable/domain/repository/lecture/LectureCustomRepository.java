package com.prgrms.coretime.timetable.domain.repository.lecture;

import com.prgrms.coretime.timetable.domain.lecture.OfficialLecture;
import com.prgrms.coretime.timetable.dto.OfficialLectureSearchCondition;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LectureCustomRepository {
  Page<OfficialLecture> findOfficialLectures(
      OfficialLectureSearchCondition officialLectureSearchCondition, Pageable pageable);

  Optional<OfficialLecture> findOfficialLectureById(Long lectureId);
}
