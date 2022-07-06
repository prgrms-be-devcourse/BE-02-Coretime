package com.prgrms.coretime.timetable.domain;

import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EnrollmentId implements Serializable {
  private Long lectureId;
  private Long timeTableId;

  public EnrollmentId(Long lectureId, Long timeTableId) {
    this.lectureId = lectureId;
    this.timeTableId = timeTableId;
  }
}
