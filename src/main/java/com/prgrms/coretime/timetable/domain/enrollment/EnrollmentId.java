package com.prgrms.coretime.timetable.domain.enrollment;

import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Embeddable
@EqualsAndHashCode
@Getter
public class EnrollmentId implements Serializable {
  private Long lectureId;
  private Long timeTableId;

  public EnrollmentId() {
  }

  public EnrollmentId(Long lectureId, Long timeTableId) {
    this.lectureId = lectureId;
    this.timeTableId = timeTableId;
  }
}
