package com.prgrms.coretime.school.domain;

import com.prgrms.coretime.common.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "school")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class School extends BaseEntity {

  @Id
  @Column(name = "school_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "name", nullable = false, length = 30)
  private String name;

  @Column(name = "email", nullable = false, length = 300)
  private String email;

  public School(Long id, String name, String email) {
    this.id = id;
    this.name = name;
    this.email = email;
  }
}
