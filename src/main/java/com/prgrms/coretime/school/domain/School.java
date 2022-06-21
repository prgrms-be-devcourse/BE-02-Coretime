package com.prgrms.coretime.school.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class School {
  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long schoolId;

  @Column(name = "name")
  private String name;

  @Column(name = "name")
  private String email;
}
