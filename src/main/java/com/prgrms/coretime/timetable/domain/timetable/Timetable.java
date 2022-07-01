package com.prgrms.coretime.timetable.domain.timetable;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

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

  @Column(name = "isDefault", nullable = false)
  private Boolean isDefault;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private User user;

  @OneToMany(mappedBy = "timeTable")
  private List<Enrollment> enrollments = new ArrayList<>();

  @Builder
  public Timetable(String name, Semester semester, Integer year, User user, Boolean isDefault) {
    validateTimetableField(name, semester, year, user, isDefault);
    this.name = name;
    this.semester = semester;
    this.year = year;
    this.user = user;
    this.isDefault = isDefault;
  }

  public void updateName(String name) {
    validateTimetableName(name);
    this.name = name;
  }

  public void makeDefault() {
    this.isDefault = true;
  }

  public void makeNonDefault() {
    this.isDefault = false;
  }

  private void validateTimetableField(String name, Semester semester, Integer year, User user, Boolean isDefault) {
    validateTimetableName(name);
    notNull(semester, "semester는 null일 수 없습니다.");
    notNull(year, "year는 null일 수 없습니다.");
    validateIsDefault(isDefault);
    notNull(user, "user는 null일 수 없습니다.");
  }

  private void validateTimetableName(String name) {
    hasText(name, "name은 null이거나 빈칸일 수 없습니다.");
    if(1 > name.length() || name.length() > 10) {
      throw new IllegalArgumentException("name의 길이는 1 ~ 10 입니다.");
    }
  }

  private void validateIsDefault(Boolean isDefault) {
    notNull(isDefault, "isDefault는 null일 수 없습니다.");
  }
}
