package com.prgrms.coretime.timetable.domain.lecture;


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

  @OneToMany(mappedBy = "lecture")
  private List<LectureDetail> lectureDetails = new ArrayList<>();

  @OneToMany(mappedBy = "lecture")
  private List<Enrollment> enrollments = new ArrayList<>();

  public Lecture(String name, String professor, String classroom) {
    this.name = name;
    this.professor = professor;
    this.classroom = classroom;
  }
}
