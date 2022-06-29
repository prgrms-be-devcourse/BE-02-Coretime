package com.prgrms.coretime.timetable.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@NoArgsConstructor
public class LectureInfo {
  private Long lectureId;
  private String name;
  private String professor;
  private String classroom;
  private List<LectureDetailInfo> lectureDetails;

  public LectureInfo(Long lectureId, String name, String professor, String classroom,
      List<LectureDetailInfo> lectureDetails) {
    this.lectureId = lectureId;
    this.name = name;
    this.professor = professor;
    this.classroom = classroom;
    this.lectureDetails = lectureDetails;
  }
}
