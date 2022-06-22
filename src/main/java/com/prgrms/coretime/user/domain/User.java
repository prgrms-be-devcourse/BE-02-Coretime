package com.prgrms.coretime.user.domain;

import com.prgrms.coretime.school.domain.School;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
public class User {

  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long userId;

  @ManyToOne
  @JoinColumn(name = "school_id")
  private School school;

  @Column(name = "email")
  private String email;

  @Column(name = "profile_image")
  private String profileImage;

  @Column(name = "nickname")
  private String nickname;

  @Column(name = "name")
  private String name;

}
