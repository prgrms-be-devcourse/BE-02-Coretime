package com.prgrms.coretime.comment.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class CommentResponse {

  private Long userId;

  private Long parentId;

  private Long commentId;

  private Integer like;

  private String name;

  private String content;

  public CommentResponse(Long userId, Long parentId, Long commentId, Integer like, String name,
      String content) {
    this.userId = userId;
    this.parentId = parentId;
    this.commentId = commentId;
    this.like = like;
    this.name = name;
    this.content = content;
  }
}

