package com.prgrms.coretime.comment.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public class CommentOneResponse extends CommentResponse {

  @QueryProjection
  public CommentOneResponse(
      Long userId,
      Long parentId,
      Long commentId,
      Integer like,
      String name,
      String content
  ) {
    super(userId, parentId, commentId, like, name, content);
  }
}
