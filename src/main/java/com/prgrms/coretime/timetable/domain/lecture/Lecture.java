package com.prgrms.coretime.timetable.domain.lecture;


import static org.springframework.util.Assert.hasText;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.timetable.domain.enrollment.Enrollment;
import com.prgrms.coretime.timetable.domain.lectureDetail.LectureDetail;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Entity
@Table(name = "lecture")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Lecture extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "lecture_id")
  private Long id;

  @Column(name = "name", length = 30)
  private String name;

  @Column(name = "professor", length = 20)
  private String professor;

  @Column(name = "classroom", length = 10)
  private String classroom;

  @Column(name = "DTYPE", nullable = false, insertable = false, updatable = false)
  private String dType;

  @OneToMany(mappedBy = "lecture")
  private List<LectureDetail> lectureDetails = new ArrayList<>();

  @OneToMany(mappedBy = "lecture")
  private List<Enrollment> enrollments = new ArrayList<>();

  public Lecture(String name, String professor, String classroom) {
    validateLectureField(name, professor, classroom);
    this.name = name;
    this.professor = professor;
    this.classroom = classroom;
  }

  public void updateName(String name) {
    validateName(name);
    this.name = name;
  }

  public void updateProfessor(String professor) {
    validateProfessor(professor);
    this.professor = professor;
  }

  public void updateClassroom(String classroom) {
    validateClassroom(classroom);
    this.classroom = classroom;
  }

  private void validateLectureField(String name, String professor, String classroom) {
    validateName(name);
    validateProfessor(professor);
    validateClassroom(classroom);
  }

  private void validateName(String name) {
    hasText(name, "name은 null이거나 빈칸일 수 없습니다.");
    if(1 > name.length() || name.length() > 30) {
      throw new IllegalArgumentException("name의 길이는 1 ~ 30 입니다.");
    }
  }

  private void validateProfessor(String professor) {
    if(professor != null) {
      hasText(professor, "professor는 빈칸일 수 없습니다.");
      if(1 > professor.length() || professor.length() > 20) {
        throw new IllegalArgumentException("professor의 길이는 1 ~ 20 입니다.");
      }
    }
  }

  private void validateClassroom(String classroom) {
    if(classroom != null) {
      hasText(classroom, "classroom은 빈칸일 수 없습니다.");
      if(1 > classroom.length() || classroom.length() > 10) {
        throw new IllegalArgumentException("classroom의 길이는 1 ~ 10 입니다.");
      }
    }
  }
}
