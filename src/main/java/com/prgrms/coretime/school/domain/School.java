package com.prgrms.coretime.school.domain;

import com.prgrms.coretime.common.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "school")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class School extends BaseEntity {

  @Id
  @Column(name = "school_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "name", nullable = false, length = 30, unique = true)
  private String name;

  @Column(name = "email", nullable = false, length = 300, unique = true)
  private String email;

  public School(String name, String email) {
    this.name = name;
    this.email = email;
  }

  // TODO : test 용
  public void setId(Long id) {
    this.id = id;
  }
}