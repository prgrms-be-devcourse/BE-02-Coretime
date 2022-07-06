package com.prgrms.coretime.timetable.domain;

import static javax.persistence.EnumType.STRING;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

import com.prgrms.coretime.school.domain.School;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
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
@DiscriminatorValue("OFFICIAL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OfficialLecture extends Lecture {

  @Enumerated(STRING)
  @Column(name = "semester", nullable = false, length = 10)
  private Semester semester;

  @Column(name = "open_year", nullable = false)
  private Integer openYear;

  @Enumerated(STRING)
  @Column(name = "grade", nullable = false)
  private Grade grade;

  @Column(name = "credit", nullable = false, length = 10)
  private Double credit;

  @Column(name = "code", nullable = false, length = 10)
  private String code;

  @Enumerated(STRING)
  @Column(name = "lecture_type", nullable = false, length = 10)
  private LectureType lectureType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "school_id", referencedColumnName = "school_id")
  private School school;

  @Builder
  public OfficialLecture(String name, String professor, String classroom, Semester semester, Integer openYear, Grade grade, Double credit, String code, LectureType lectureType) {
    super(name, professor, classroom);

    validateOfficialLecture(semester, openYear, grade, credit, code, lectureType);
    this.semester = semester;
    this.openYear = openYear;
    this.grade = grade;
    this.credit = credit;
    this.code = code;
    this.lectureType = lectureType;
  }

  public void setSchool(School school) {
    this.school = school;
  }

  public boolean canEnrol(Long schoolId) {
    if(this.school.getId() != schoolId) {
      return false;
    }else{
      return true;
    }
  }

  private void validateOfficialLecture(Semester semester, Integer openYear, Grade grade, Double credit, String code, LectureType lectureType) {
    validateSemester(semester);
    validateOpenYear(openYear);
    validateGrade(grade);
    validateCredit(credit);
    validateCode(code);
    validateLectureType(lectureType);
  }

  private void validateSemester(Semester semester) {
    notNull(semester, "semester는 null일 수 없습니다.");
  }

  private void validateOpenYear(Integer openYear) {
    notNull(openYear, "openYear는 null일 수 없습니다.");

    if(openYear <= 0) {
      throw new IllegalArgumentException("openYear는 0보다 작거나 같을 수 없습니다.");
    }
  }

  private void validateGrade(Grade grade) {
    notNull(grade, "grade는 null일 수 없습니다.");
  }

  private void validateCredit(Double credit) {
    notNull(credit, "credit는 null일 수 없습니다.");
  }

  public void validateCode(String code) {
    hasText(code, "code는 null이거나 빈칸일 수 없습니다");
    if(1 > code.length() || code.length() > 10) {
      throw new IllegalArgumentException("code는 길이는 1 ~ 10 입니다.");
    }
  }

  public void validateLectureType(LectureType lectureType) {
    notNull(lectureType, "lectureType은 null일 수 없습니다.");
  }
}
