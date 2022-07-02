package com.prgrms.coretime.comment.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentsResponse {

  private Long userId;

  private Long parentId;

  private Long commentId;

  private Long like;

  private int seq;

  private String content;

  private List<CommentsResponse> children = new ArrayList<>();

  @QueryProjection
  public CommentsResponse(Long userId, Long parentId, Long commentId, Long like, int seq,
      String content) {
    this.userId = userId;
    this.parentId = parentId;
    this.like = like;
    this.commentId = commentId;
    this.seq = seq;
    this.content = content;
  }

  public void setChildren(List<CommentsResponse> children) {
    this.children = children;
  }

}
