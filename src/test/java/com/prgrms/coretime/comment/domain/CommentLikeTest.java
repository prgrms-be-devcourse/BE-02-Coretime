package com.prgrms.coretime.comment.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardType;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CommentLikeTest {

  private User user = new User("example@email.com", "example");

  School school = new School("university", "university@university.ac.kr");

  private Board board = Board.builder()
      .category(BoardType.BASIC)
      .name("게시판")
      .school(school)
      .build();

  private Post post = Post.builder()
      .user(user)
      .board(board)
      .title("title")
      .content("게시글 내용입니다.")
      .isAnonymous(true)
      .build();

  private String content = "이것은 공백도 아니며, 300바이트를 넘지 않아요!";

  private Comment comment = Comment.builder()
      .user(user)
      .post(post)
      .parent(null)
      .isAnonymous(true)
      .content(content)
      .build();

  @Nested
  @DisplayName("edge case 중에서 ")
  class Describe_EdgeCase {

    @Test
    @DisplayName("user가 null일 경우")
    public void testUserNull() {
      Assertions.assertThatThrownBy(this::callUserNull)
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("사용자는 필수입니다.");
    }


    @Test
    @DisplayName("comment가 null일 경우")
    public void testCommentNull() {
      Assertions.assertThatThrownBy(this::callCommentNull)
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("댓글은 필수입니다.");
    }

    private void callUserNull() {
      new CommentLike(null, comment);
    }

    private void callCommentNull() {
      new CommentLike(user, null);
    }
  }

  @Nested
  @DisplayName("happy path 중에서")
  class Describe_HappyPath {

    @Test
    @DisplayName("user와 comment 모두 존재 할 경우에는 통과!")
    public void testCorrectCreation() {
      CommentLike commentLike = new CommentLike(user, comment);
      assertThat(commentLike).isInstanceOf(CommentLike.class);
    }

  }
}