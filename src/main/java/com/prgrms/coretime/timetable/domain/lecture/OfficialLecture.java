package com.prgrms.coretime.timetable.domain.lecture;

import static javax.persistence.EnumType.STRING;

import com.prgrms.coretime.timetable.domain.Semester;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "official_lecture")
@NoArgsConstructor
public class OfficialLecture extends Lecture{
  @Enumerated(STRING)
  @Column(nullable = false, length = 10)
  private Semester semester;

  @Column(name = "open_year", nullable = false)
  private int openYear;

  @Enumerated(STRING)
  @Column(nullable = false)
  private Grade grade;

  @Column(nullable = false)
  private double credit;

  @Column(nullable = false, length = 10)
  private String code;

  @Enumerated(STRING)
  @Column(nullable = false, length = 10)
  private LectureType lectureType;

  // private School school;

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
}
