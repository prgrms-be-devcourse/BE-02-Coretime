package com.prgrms.coretime.timetable.domain.repository.lectureDetail;

public interface LectureDetailCustomRepository {
  void deleteCustomLectureDetailsByLectureId(Long lectureId);
}
