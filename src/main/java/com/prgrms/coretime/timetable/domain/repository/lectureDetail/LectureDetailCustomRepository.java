package com.prgrms.coretime.timetable.domain.repository.lectureDetail;

public interface LectureDetailCustomRepository {
  void deleteCustomLecturesByLectureId(Long lectureId);
}
