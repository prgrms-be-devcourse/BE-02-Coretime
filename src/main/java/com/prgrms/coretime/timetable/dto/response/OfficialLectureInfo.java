package com.prgrms.coretime.timetable.dto.response;

import com.prgrms.coretime.timetable.domain.lecture.LectureType;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@NoArgsConstructor
public class OfficialLectureInfo extends LectureInfo{
  private String code;
  private Double credit;
  private LectureType lectureType;

  public OfficialLectureInfo(Long lectureId, String name, String professor,
      String classroom,
      List<LectureDetailInfo> lectureDetails, String code, Double credit,
      LectureType lectureType) {
    super(lectureId, name, professor, classroom, lectureDetails);
    this.code = code;
    this.credit = credit;
    this.lectureType = lectureType;
  }
}
