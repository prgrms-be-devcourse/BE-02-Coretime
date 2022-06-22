package com.prgrms.coretime.timetable.domain.lectureDetail;

import static javax.persistence.FetchType.LAZY;

import com.prgrms.coretime.timetable.domain.lecture.Lecture;
import java.time.LocalTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lecture_detail")
@NoArgsConstructor
@Getter
public class LectureDetail {
  @Id
  @GeneratedValue
  @Column(name = "lecture_detail_id")
  private Long id;

  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  @Column(nullable = false)
  private Day day;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name=  "lecture_id", referencedColumnName = "lecture_id")
  private Lecture lecture;

  @Builder
  public LectureDetail(LocalTime startTime, LocalTime endTime,
      Day day) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.day = day;
  }

  public void addUser(Lecture lecture) {
    if(Objects.nonNull(this.lecture)) {
      this.lecture.getLectureDetails().remove(this.lecture);
    }
    this.lecture = lecture;
    lecture.getLectureDetails().add(this);
  }
}
