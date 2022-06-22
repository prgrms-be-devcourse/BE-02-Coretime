package com.prgrms.coretime.timetable.domain;

import static javax.persistence.EnumType.STRING;

import com.prgrms.coretime.timetable.domain.enrollment.Enrollment;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "timetable")
@NoArgsConstructor
@Getter
public class TimeTable {
  @Id
  @GeneratedValue
  @Column(name="timetable_id")
  private Long id;

  @Column(nullable = false, length = 10)
  private String name;

  @Enumerated(STRING)
  @Column(nullable = false, length = 10)
  private Semester semester;

  @Column(nullable = false)
  private int year;

  // private User user;

  @OneToMany(mappedBy = "timeTable")
  private List<Enrollment> enrollments = new ArrayList<>();

  @Builder
  public TimeTable(String name, Semester semester, int year) {
    this.name = name;
    this.semester = semester;
    this.year = year;
  }
}
