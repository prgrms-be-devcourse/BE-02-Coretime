package com.prgrms.coretime.timetable.dto.request;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EnrollmentCreateRequest {
  @NotNull
  private Long lectureId;

  public EnrollmentCreateRequest(Long lectureId) {
    this.lectureId = lectureId;
  }
}
