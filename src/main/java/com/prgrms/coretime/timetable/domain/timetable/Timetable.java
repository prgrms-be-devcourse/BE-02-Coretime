package com.prgrms.coretime.timetable.domain.timetable;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static org.springframework.util.Assert.*;
import static org.springframework.util.Assert.hasText;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.timetable.domain.Semester;
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
import org.springframework.util.Assert;

@Entity
@Table(name = "timetable")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Timetable extends BaseEntity {

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
  private Integer year;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private User user;

  @OneToMany(mappedBy = "timeTable")
  private List<Enrollment> enrollments = new ArrayList<>();

  @Builder
  public Timetable(String name, Semester semester, Integer year) {
    validateTimetableField(name, semester, year);
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

  private void validateTimetableField(String name, Semester semester, Integer year) {
    hasText(name, "name cannot be null blank");
    if(1 > name.length() || name.length() > 10) {
      throw new IllegalArgumentException("1 <= name <= 10");
    }

    notNull(semester, "semester cannot be null");

    notNull(year, "year cannot be null");
  }
}
