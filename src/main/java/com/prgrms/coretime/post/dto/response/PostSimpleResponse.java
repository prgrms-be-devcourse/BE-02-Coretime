package com.prgrms.coretime.post.dto.response;

import com.prgrms.coretime.post.domain.Post;
import lombok.Builder;

@Builder
public record PostSimpleResponse(Long postId, BoardSimpleResponse board,
                                 UserSimpleResponse user, String title,
                                 String content, Boolean isAnonymous,
                                 Integer commentCount, Integer likeCount) {

  public PostSimpleResponse(Post entity) {
    this(
        entity.getId(),
        new BoardSimpleResponse(entity.getBoard()),
        new UserSimpleResponse(entity.getUser()),
        entity.getTitle(),
        entity.getContent(),
        entity.getIsAnonymous(),
        entity.getCommentCount(),
        entity.getLikeCount()
    );
  }
}
