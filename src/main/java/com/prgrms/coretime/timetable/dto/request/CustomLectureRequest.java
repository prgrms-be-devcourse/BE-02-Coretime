package com.prgrms.coretime.timetable.dto.request;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomLectureRequest {
  @NotBlank
  private String name;

  private String professor;

  private String classroom;

  @Valid
  @NotEmpty
  private List<CustomLectureDetail> lectureDetails;

  @Builder
  public CustomLectureRequest(String name, String professor, String classroom,
      List<CustomLectureDetail> lectureDetails) {
    this.name = name;
    this.professor = professor;
    this.classroom = classroom;
    this.lectureDetails = lectureDetails;
  }
}
