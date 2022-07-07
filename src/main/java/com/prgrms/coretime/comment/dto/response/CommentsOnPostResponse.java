package com.prgrms.coretime.comment.dto.response;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.post.domain.Post;
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
    String name = getName(comment, user);

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

  private static String getName(Comment comment, User user) {
    Post post = comment.getPost();

    if (user == null) {
      return "(알 수 없음)";
    }

    if (user != null && !comment.getIsAnonymous()) {
      return user.getNickname();
    }

    if (comment.getAnonymous() && comment.getPost().getIsAnonymous()
        && user.getId() == post.getUser().getId()) {
      return "익명(글쓴이)";
    }

    return "익명" + comment.getAnonymousSeq();
  }

  public int getChildrenSize() {
    return children.size();
  }
}
