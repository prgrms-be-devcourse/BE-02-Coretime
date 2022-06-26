package com.prgrms.coretime.timetable.dto.request;

import com.prgrms.coretime.timetable.domain.Semester;
import com.prgrms.coretime.timetable.domain.lecture.LectureType;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OfficialLectureRequest {
  @NotNull
  private String name;

  private String professor;

  private String classRoom;

  @NotNull
  private Semester semester;

  @NotNull
  private Integer year;

  @NotNull
  private Double credit;

  @NotNull
  private String code;

  @NotNull
  private LectureType lectureType;

  @Valid
  @NotEmpty
  private List<LectureDetailRequest> lectureDetails;

  @Builder
  public OfficialLectureRequest(String name, String professor, String classRoom,
      Semester semester, Integer year, Double credit, String code,
      LectureType lectureType,
      List<LectureDetailRequest> lectureDetails) {
    this.name = name;
    this.professor = professor;
    this.classRoom = classRoom;
    this.semester = semester;
    this.year = year;
    this.credit = credit;
    this.code = code;
    this.lectureType = lectureType;
    this.lectureDetails = lectureDetails;
  }
}
