package com.prgrms.coretime.comment.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class CommentLikeId implements Serializable {

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "comment_id")
  private Long commentId;

  public CommentLikeId(Long userId, Long commentId) {
    this.userId = userId;
    this.commentId = commentId;
  }
}
