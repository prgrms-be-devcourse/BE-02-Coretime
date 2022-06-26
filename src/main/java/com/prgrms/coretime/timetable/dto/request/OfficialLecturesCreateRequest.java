package com.prgrms.coretime.timetable.dto.request;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OfficialLecturesCreateRequest {
  @Valid
  @NotEmpty
  private List<OfficialLectureRequest> lectures;

  public OfficialLecturesCreateRequest(List<OfficialLectureRequest> lectures) {
    this.lectures = lectures;
  }
}
