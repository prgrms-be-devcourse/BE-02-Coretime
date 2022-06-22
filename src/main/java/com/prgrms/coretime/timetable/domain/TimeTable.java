package com.prgrms.coretime.timetable.domain;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.timetable.domain.enrollment.Enrollment;
import com.prgrms.coretime.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "timetable")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TimeTable extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "timetable_id")
  private Long id;

  @Column(name = "name", nullable = false, length = 10)
  private String name;

  @Enumerated(STRING)
  @Column(name = "semester", nullable = false, length = 10)
  private Semester semester;

  @Column(name = "year", nullable = false)
  private int year;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private User user;

  @OneToMany(mappedBy = "timeTable")
  private List<Enrollment> enrollments = new ArrayList<>();

  @Builder
  public TimeTable(String name, Semester semester, int year) {
    this.name = name;
    this.semester = semester;
    this.year = year;
  }

  public void setUser(User user) {
    if (Objects.nonNull(this.user)) {
      //
    }
    this.user = user;
  }
}
