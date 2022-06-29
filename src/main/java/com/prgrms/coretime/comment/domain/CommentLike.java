package com.prgrms.coretime.comment.domain;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.user.domain.User;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.springframework.util.Assert;

@Entity
@Table(name = "comment_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLike extends BaseEntity {

  @EmbeddedId
  private CommentLikeId id;

  @MapsId("userId")
  @ManyToOne
  @JoinColumnOrFormula(
      column = @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  )
  private User user;

  @MapsId("commentId")
  @ManyToOne
  @JoinColumnOrFormula(
      column = @JoinColumn(name = "comment_id", referencedColumnName = "comment_id")
  )
  private Comment comment;

  public CommentLike(User user, Comment comment) {
    Assert.notNull(user, "사용자는 필수입니다.");
    Assert.notNull(comment, "댓글은 필수입니다.");
    this.user = user;
    this.comment = comment;
  }
}