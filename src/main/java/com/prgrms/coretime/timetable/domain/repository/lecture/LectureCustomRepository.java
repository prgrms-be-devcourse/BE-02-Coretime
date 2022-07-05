package com.prgrms.coretime.timetable.domain.repository.lecture;

import com.prgrms.coretime.timetable.domain.OfficialLecture;
import com.prgrms.coretime.timetable.domain.LectureDetail;
import com.prgrms.coretime.timetable.dto.OfficialLectureSearchCondition;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LectureCustomRepository {
  Page<OfficialLecture> getOfficialLectures(
      OfficialLectureSearchCondition officialLectureSearchCondition, Pageable pageable);

  Optional<OfficialLecture> getOfficialLectureById(Long lectureId);

  long getNumberOfTimeOverlapLectures(Long timetableId, List<LectureDetail> lectureDetails, List<Long> lectureDetailIds);

  boolean isCustomLecture(Long lectureId);

  void deleteLectureByLectureIds(List<Long> lectureIds);
}
