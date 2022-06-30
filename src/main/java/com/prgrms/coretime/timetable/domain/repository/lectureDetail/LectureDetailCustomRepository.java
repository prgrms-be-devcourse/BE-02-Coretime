package com.prgrms.coretime.timetable.domain.repository.lectureDetail;

import java.util.List;

public interface LectureDetailCustomRepository {
  void deleteCustomLectureDetailsByLectureId(Long lectureId);

  void deleteLectureDetailsByLectureIds(List<Long> lectureIds);
}
