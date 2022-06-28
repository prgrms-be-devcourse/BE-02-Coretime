package com.prgrms.coretime.timetable.dto.response;

import com.prgrms.coretime.timetable.domain.lecture.LectureType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OfficialLectureInfo {
  private Long lectureId;
  private String name;
  private String professor;
  private String code;
  private Double credit;
  private LectureType lectureType;
  private List<LectureDetailInfo> lectureDetails;

  @Builder
  public OfficialLectureInfo(Long lectureId, String name, String professor, String code,
      Double credit, LectureType lectureType,
      List<LectureDetailInfo> lectureDetails) {
    this.lectureId = lectureId;
    this.name = name;
    this.professor = professor;
    this.code = code;
    this.credit = credit;
    this.lectureType = lectureType;
    this.lectureDetails = lectureDetails;
  }
}
