package com.prgrms.coretime.post.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Photo {

  @Id
  @GeneratedValue
  @Column(name = "photo_id")
  private Long id;

  @Column(name = "path", nullable = false, length = 300)
  private String path;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", referencedColumnName = "post_id")
  private Post post;

  public Photo(String path, Post post) {
    setPath(path);
    setPost(post);
  }

  private void setPath(String path) {
    if (Objects.isNull(path)) {
      throw new IllegalArgumentException("Photo의 path은 null일 수 없습니다.");
    }else if (path.isBlank()) {
      throw new IllegalArgumentException("Photo의 path은 빈 문자열일 수 없습니다.");
    }else if (path.length() > 300) {
      throw new IllegalArgumentException("Photo의 path은 300글자를 넘을 수 없습니다.");
    }
    this.path = path;
  }

  private void setPost(Post post) {
    if (Objects.isNull(post)) {
      throw new IllegalArgumentException("Photo의 post는 null일 수 없습니다.");
    }
    if (Objects.nonNull(this.post)) {
      this.post.getPhotos().remove(this);
    }
    post.getPhotos().add(this);
    this.post = post;
  }
}
