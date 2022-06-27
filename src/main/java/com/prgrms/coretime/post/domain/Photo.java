package com.prgrms.coretime.post.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Photo {

  @Id
  @GeneratedValue
  @Column(name = "photo_id")
  private Long id;

  @Column(name = "path", length = 300)
  private String path;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", referencedColumnName = "post_id")
  private Post post;
}
