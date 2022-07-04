package com.prgrms.coretime.comment.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentsOnPostResponse extends CommentResponse {

  private List<CommentsOnPostResponse> children = new ArrayList<>();

  @QueryProjection
  public CommentsOnPostResponse(
      Long userId,
      Long parentId,
      Long commentId,
      Long like,
      String name,
      String content
  ) {
    super(userId, parentId, commentId, like, name, content);
  }

  public void setChildren(List<CommentsOnPostResponse> children) {
    this.children = children;
  }
}
