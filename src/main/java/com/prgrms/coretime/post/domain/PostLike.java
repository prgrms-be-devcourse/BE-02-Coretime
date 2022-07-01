package com.prgrms.coretime.post.domain;

import com.prgrms.coretime.common.ErrorCode;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.user.domain.User;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinColumnOrFormula;

import javax.persistence.*;

@Entity
@Table(name = "post_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostLike {

  @EmbeddedId
  private PostLikeId postLikeId;

  @MapsId("postId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumnOrFormula(column =
  @JoinColumn(name = "post_id",
      referencedColumnName = "post_id")
  )
  private Post post;

  @MapsId("userId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumnOrFormula(column =
  @JoinColumn(name = "user_id",
      referencedColumnName = "user_id")
  )
  private User user;

  public PostLike(Post post, User user) {
    setPost(post);
    setUser(user);
    this.postLikeId = new PostLikeId(post.getId(), user.getId());
  }

  private void setPost(Post post) {
    if (Objects.isNull(post)) {
      throw new NotFoundException(ErrorCode.POST_NOT_FOUND);
    }
    this.post = post;
    post.likePost();
  }

  private void setUser(User user) {
    if (Objects.isNull(user)) {
      throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
    }
    this.user = user;
  }
}
