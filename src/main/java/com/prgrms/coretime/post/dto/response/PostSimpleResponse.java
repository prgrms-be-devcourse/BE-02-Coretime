package com.prgrms.coretime.post.dto.response;

import com.prgrms.coretime.post.domain.Post;
import lombok.Builder;

@Builder
public record PostSimpleResponse(Long postId, BoardSimpleResponse board,
                                 Long userId, String nickname, String title,
                                 String content, Boolean isAnonymous,
                                 Integer commentCount, Integer likeCount) {

  public PostSimpleResponse(Post entity) {
    this(
        entity.getId(),
        new BoardSimpleResponse(entity.getBoard()),
        entity.getUser().getId(),
        entity.getIsAnonymous() ? "익명" : entity.getUser().getNickname(),
        entity.getTitle(),
        entity.getContent(),
        entity.getIsAnonymous(),
        entity.getCommentCount(),
        entity.getLikeCount()
    );
  }
}
