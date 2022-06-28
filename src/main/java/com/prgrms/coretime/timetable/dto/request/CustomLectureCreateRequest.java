package com.prgrms.coretime.timetable.dto.request;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomLectureCreateRequest {
  @NotBlank
  private String name;

  private String professor;

  private String classRoom;

  @Valid
  private List<CustomLectureDetail> lectureDetails;

  @Builder
  public CustomLectureCreateRequest(String name, String professor, String classRoom,
      List<CustomLectureDetail> lectureDetails) {
    this.name = name;
    this.professor = professor;
    this.classRoom = classRoom;
    this.lectureDetails = lectureDetails;
  }
}
