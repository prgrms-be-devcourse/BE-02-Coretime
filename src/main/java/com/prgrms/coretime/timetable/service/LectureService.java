package com.prgrms.coretime.timetable.service;

import com.prgrms.coretime.timetable.dto.request.OfficialLecturesCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LectureService {
  @Transactional
  public void addOfficialLectures(OfficialLecturesCreateRequest officialLecturesCreateRequest) {

  }
}
