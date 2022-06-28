package com.prgrms.coretime.timetable.domain.lectureDetail;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static org.springframework.util.Assert.notNull;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.timetable.domain.lecture.Lecture;
import java.time.LocalTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Table(name = "lecture_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LectureDetail extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "lecture_detail_id")
  private Long id;

  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  @Enumerated(STRING)
  @Column(name = "day", length = 3, nullable = false)
  private Day day;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name=  "lecture_id", referencedColumnName = "lecture_id")
  private Lecture lecture;

  @Builder
  public LectureDetail(LocalTime startTime, LocalTime endTime, Day day) {
    validateLectureDetailField(startTime, endTime, day);
    this.startTime = startTime;
    this.endTime = endTime;
    this.day = day;
  }

  public void setLecture(Lecture lecture) {
    if(Objects.nonNull(this.lecture)) {
      this.lecture.getLectureDetails().remove(this.lecture);
    }
    this.lecture = lecture;
    lecture.getLectureDetails().add(this);
  }

  private void validateLectureDetailField(LocalTime startTime, LocalTime endTime, Day day) {
    validateStartTime(startTime);
    validateEndTime(endTime);
    validateDay(day);
    validateStartTimeEndTime(startTime, endTime);
  }

  private void validateStartTime(LocalTime startTime) {
    notNull(startTime, "startTime은 null일 수 없습니다.");
    validateTimeFormat(startTime);
  }

  private void validateEndTime(LocalTime endTime) {
    notNull(endTime, "endTime은 null일 수 없습니다.");
    validateTimeFormat(endTime);
  }

  private void validateDay(Day day) {
    notNull(day, "day는 null일 수 없습니다.");
  }

  private void validateStartTimeEndTime(LocalTime startTime, LocalTime endTime) {
    if(startTime.isAfter(endTime) || startTime.equals(endTime)) {
      throw new IllegalArgumentException("startTime은 endTime 보다 빨라야합니다.");
    }
  }

  private void validateTimeFormat(LocalTime time) {
    if(time.getMinute() % 5 != 0 || time.getSecond() != 0 || time.getNano() != 0) {
      throw new IllegalArgumentException("잘못된 시간 포맷입니다.");
    }
  }
}
