package com.prgrms.coretime.timetable.domain;

import static javax.persistence.FetchType.LAZY;

import com.prgrms.coretime.common.entity.BaseEntity;
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

  public Enrollment(Lecture lecture, Timetable timetable) {
    this.enrollmentId = new EnrollmentId(lecture.getId(), timetable.getId());
    setLecture(lecture);
    setTimeTable(timetable);
  }

  private void setLecture(Lecture lecture) {
    this.lecture = lecture;
    lecture.getEnrollments().add(this);
  }

  private void setTimeTable(Timetable timetable) {
    this.timeTable = timetable;
    timetable.getEnrollments().add(this);
  }
}
