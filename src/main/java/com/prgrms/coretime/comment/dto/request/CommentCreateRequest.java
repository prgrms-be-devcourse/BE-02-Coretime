package com.prgrms.coretime.comment.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentCreateRequest {

  @NotNull(message = "게시글 ID는 필수입니다.")
  private Long postId;

  private Long parentId;

  @NotNull(message = "댓글 익명 여부는 필수 입니다.")
  private Boolean isCommentAnonymous;

  @NotBlank(message = "댓글 내용은 필수 입니다.")
  private String content;

  @Builder
  public CommentCreateRequest(
      Long postId,
      Long parentId,
      Boolean isCommentAnonymous,
      String content) {
    this.postId = postId;
    this.parentId = parentId;
    this.isCommentAnonymous = isCommentAnonymous;
    this.content = content;
  }
}
