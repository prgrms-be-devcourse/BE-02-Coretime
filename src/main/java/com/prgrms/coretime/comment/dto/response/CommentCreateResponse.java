package com.prgrms.coretime.comment.dto.response;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreateResponse {

  private Long userId;

  private Long postId;

  private Long parentId;

  private Long commentId;

  private Integer seq;

  private Boolean isPostAnonymous;

  private Boolean isCommentAnonymous;

  private String content;

  @Builder
  private CommentCreateResponse(Long userId, Long postId, Long parentId,
      Long commentId, Integer seq, Boolean isPostAnonymous, Boolean isCommentAnonymous,
      String content) {
    this.userId = userId;
    this.postId = postId;
    this.parentId = parentId;
    this.commentId = commentId;
    this.seq = seq;
    this.isPostAnonymous = isPostAnonymous;
    this.isCommentAnonymous = isCommentAnonymous;
    this.content = content;
  }

  public static CommentCreateResponse of(User user, Post post, Comment comment) {
    Comment parent = comment.getParent();
    Long parentId = parent == null ? null : parent.getId();

    return CommentCreateResponse.builder()
        .userId(user.getId())
        .postId(post.getId())
        .parentId(parentId) // nullable
        .commentId(comment.getId())
        .seq(comment.getAnonymousSeq()) // nullable
        .content(comment.getContent())
        .build();
  }
}
