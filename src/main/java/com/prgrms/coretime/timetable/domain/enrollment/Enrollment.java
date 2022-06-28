package com.prgrms.coretime.timetable.domain.enrollment;

import static javax.persistence.FetchType.LAZY;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.timetable.domain.timetable.Timetable;
import com.prgrms.coretime.timetable.domain.lecture.Lecture;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Enrollment extends BaseEntity {

  @EmbeddedId
  private EnrollmentId enrollmentId;

  @MapsId("lectureId")
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "lecture_id", referencedColumnName = "lecture_id")
  private Lecture lecture;

  @MapsId("timeTableId")
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name="timetable_id", referencedColumnName = "timetable_id")
  private Timetable timeTable;

  public Enrollment(EnrollmentId enrollmentId) {
    this.enrollmentId = enrollmentId;
  }

  public void setLecture(Lecture lecture) {
    this.lecture = lecture;
  }

  public void setTimeTable(Timetable timetable) {
    this.timeTable = timetable;
  }
}
