package com.prgrms.coretime.post.dto.response;

import com.prgrms.coretime.comment.domain.Comment;
import lombok.Builder;

public record CommentResponse(Long commentId, Long userId, String nickname,
                              String content, Boolean isDelete, Boolean isAnonymous) {

  @Builder
  public CommentResponse {
  }

  public CommentResponse(Comment entity) {
    this(
        entity.getId(),
        entity.getUser().getId(),
        entity.getIsAnonymous() ? "익명" + entity.getAnonymousSeq() : entity.getUser().getNickname(),
        entity.getContent(),
        entity.getIsDelete(),
        entity.getIsAnonymous()
    );
  }
}
