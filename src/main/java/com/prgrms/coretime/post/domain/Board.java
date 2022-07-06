package com.prgrms.coretime.post.domain;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.school.domain.School;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "board")
public class Board extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "board_id")
  private Long id;

  @Column(name = "name", nullable = false, length = 10)
  private String name;

  @Column(name = "category", nullable = false)
  @Enumerated(EnumType.STRING)
  private BoardType category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "school_id")
  private School school;

  @Builder
  public Board(String name, BoardType category, School school) {
    setName(name);
    setCategory(category);
    this.school = school;
  }

  private void setName(String name) {
    if (Objects.isNull(name)) {
      throw new IllegalArgumentException("Board의 name은 null일 수 없습니다.");
    }else if (name.isBlank()) {
      throw new IllegalArgumentException("Board의 name은 빈 문자열일 수 없습니다.");
    }else if (name.length() > 10) {
      throw new IllegalArgumentException("Board의 name은 10글자를 넘을 수 없습니다.");
    }
    this.name = name;
  }

  private void setCategory(BoardType category) {
    if (Objects.isNull(category)) {
      throw new IllegalArgumentException("Board의 category는 null일 수 없습니다.");
    }
    this.category = category;
  }

  private void setSchool(School school) {
    if (Objects.isNull(school)) {
      throw new IllegalArgumentException("Board의 Shool은 null일 수 없습니다.");
    }
    this.school = school;
  }
}
