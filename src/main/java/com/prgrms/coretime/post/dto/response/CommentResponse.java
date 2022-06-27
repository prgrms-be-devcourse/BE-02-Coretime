package com.prgrms.coretime.post.dto.response;

import com.prgrms.coretime.comment.domain.Comment;
import lombok.Builder;

public record CommentResponse(Long commentId, UserSimpleResponse user,
                              String content, Boolean isDelete, Boolean isAnonymous) {

  @Builder
  public CommentResponse {
  }

  public CommentResponse(Comment entity) {
    this(
        entity.getId(),
        new UserSimpleResponse(entity.getUser()),
        entity.getContent(),
        entity.getIsDelete(),
        entity.getIsAnonymous()
    );
  }
}
