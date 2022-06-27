package com.prgrms.coretime.post.dto.response;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.post.domain.Post;
import lombok.Builder;
import org.springframework.data.domain.Page;

public record PostResponse(Long postId, BoardSimpleResponse board,
                           UserSimpleResponse user, String title,
                           String content, Boolean isAnonymous,
                           Page<CommentResponse> comments,
                           Integer likeCount) {

  @Builder
  public PostResponse {
  }

  public PostResponse(Post entity, Page<Comment> comments) {
    this(
        entity.getId(),
        new BoardSimpleResponse(entity.getBoard()),
        new UserSimpleResponse(entity.getUser()),
        entity.getTitle(),
        entity.getContent(),
        entity.getIsAnonymous(),
        comments.map(CommentResponse::new),
        entity.getLikeCount()
    );
  }
}
