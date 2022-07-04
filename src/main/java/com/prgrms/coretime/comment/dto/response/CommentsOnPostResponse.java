package com.prgrms.coretime.comment.dto.response;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.user.domain.User;
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
      Integer like,
      String name,
      String content
  ) {
    super(userId, parentId, commentId, like, name, content);
  }

  public void setChildren(List<CommentsOnPostResponse> children) {
    this.children = children;
  }

  public static CommentsOnPostResponse of(Comment comment) {
    User user = comment.getUser();
    Comment parent = comment.getParent();
    String name = "익명" + comment.getAnonymousSeq();
    if (user == null)
      name = "(알 수 없음)";

    if (user != null && !comment.getIsAnonymous())
      name = user.getNickname();

    String content = comment.getIsDelete() || user == null ? "삭제된 댓글입니다." : comment.getContent();

    return new CommentsOnPostResponse(
        user == null ? null : user.getId(),
        parent == null ? null : parent.getId(),
        comment.getId(),
        comment.getLikes().size(),
        name,
        content
    );
  }

}
