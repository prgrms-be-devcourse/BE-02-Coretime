package com.prgrms.coretime.timetable.domain.lecture;

import static javax.persistence.EnumType.STRING;

import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.timetable.domain.Semester;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "official_lecture")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OfficialLecture extends Lecture {

  @Enumerated(STRING)
  @Column(name = "semester", nullable = false, length = 10)
  private Semester semester;

  @Column(name = "open_year", nullable = false)
  private int openYear;

  @Enumerated(STRING)
  @Column(name = "grade", nullable = false)
  private Grade grade;

  @Column(name = "credit", nullable = false)
  private double credit;

  @Column(name = "code", nullable = false, length = 10)
  private String code;

  @Enumerated(STRING)
  @Column(name = "lecture_type", nullable = false, length = 10)
  private LectureType lectureType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "school_id", referencedColumnName = "school_id")
  private School school;

  @Builder
  public OfficialLecture(String name, String professor, String classroom,
      Semester semester, int openYear, Grade grade, double credit, String code,
      LectureType lectureType) {
    super(name, professor, classroom);
    this.semester = semester;
    this.openYear = openYear;
    this.grade = grade;
    this.credit = credit;
    this.code = code;
    this.lectureType = lectureType;
  }

  public void setSchool(School school) {
    if (Objects.nonNull(this.school)) {
      //
    }
    this.school = school;
  }
}
