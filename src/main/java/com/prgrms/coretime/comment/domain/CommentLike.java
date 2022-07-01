package com.prgrms.coretime.comment.domain;

import static javax.persistence.FetchType.LAZY;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.user.domain.User;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.springframework.util.Assert;

@Entity
@Table(name = "comment_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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
  @ManyToOne(fetch = LAZY)
  @JoinColumnOrFormula(
      column = @JoinColumn(name = "comment_id", referencedColumnName = "comment_id")
  )
  private Comment comment;

  public CommentLike(User user, Comment comment) {
    Assert.notNull(user, "사용자는 필수입니다.");
    Assert.notNull(comment, "댓글은 필수입니다.");
    this.id = new CommentLikeId(user.getId(), comment.getId());
    this.user = user;
    setComment(comment);
  }

  public void setComment(Comment comment) {
    this.comment = comment;
    comment.getLikes().add(this);
  }
}
