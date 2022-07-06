package com.prgrms.coretime.comment.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.user.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class CommentTest {

  private User user = new User("example@email.com", "example");

  private Post post = Post.builder()
      .title("title")
      .content("게시글 내용입니다.")
      .isAnonymous(true)
      .build();

  private String correctContent = "이것은 공백도 아니며, 300바이트를 넘지 않아요!";

  String _300BytesString = "이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어."
      + "이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어."
      + "이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어."
      + "이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어."
      + "이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어..";

  String _301BytesString = "이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어."
      + "이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어."
      + "이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어."
      + "이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어."
      + "이거는300길이가넘어.이거는300길이가넘어..";


  @Nested
  @DisplayName("edge case 중에서 ")
  class Describe_EdgeCase {

    @Test
    @DisplayName("1. user가 null일 경우")
    public void testUserNull() {
      assertThatThrownBy(() -> {
            Comment.builder()
                .user(null)
                .post(post)
                .parent(null)
                .isAnonymous(true)
                .content(correctContent)
                .build();
          }
      ).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * TODO : 인증 구현 완료 후 추후 검증
     */
    @Test
    @DisplayName("2. user가 현재 로그인한 유저가 맞는지")
    public void testUserIsCurrentUser() {
    }

    @Test
    @DisplayName("3. post가 null일 경우")
    public void testPostNull() {
      assertThatThrownBy(() -> {
            Comment.builder()
                .user(user)
                .post(null)
                .parent(null)
                .isAnonymous(true)
                .content(correctContent)
                .build();
          }
      ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("4. content가 null 일경우")
    public void testContentNull() {
      assertThatThrownBy(() -> {
            Comment.builder()
                .user(user)
                .post(post)
                .parent(null)
                .isAnonymous(true)
                .content(null)
                .build();
          }
      ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("5. content가 공백일 경우")
    public void testContentBlank() {
      Assertions.assertAll(
          () -> assertThatThrownBy(() -> {
                Comment.builder()
                    .user(user)
                    .post(post)
                    .parent(null)
                    .isAnonymous(true)
                    .content("   ")
                    .build();
              }
          ).isInstanceOf(IllegalArgumentException.class),
          () -> assertThatThrownBy(() -> {
                Comment.builder()
                    .user(user)
                    .post(post)
                    .parent(null)
                    .isAnonymous(true)
                    .content("")
                    .build();
              }
          ).isInstanceOf(IllegalArgumentException.class)
      );
    }

    @Test
    @DisplayName("6. content가 300바이트 넘길 경우")
    public void testLongContent() {

      Assertions.assertAll(
          () -> {
            assertThat(_301BytesString.length()).isEqualTo(301);
          },
          () -> assertThatThrownBy(() -> {
                Comment.builder()
                    .user(user)
                    .post(post)
                    .parent(null)
                    .isAnonymous(true)
                    .content(_301BytesString)
                    .build();
              }
          ).isInstanceOf(IllegalArgumentException.class)
      );
    }

    @Test
    @DisplayName("7. isAnonymous : 익명 여부가 null일 경우")
    public void testIsAnonumousNull() {
      assertThatThrownBy(() -> {
            Comment.builder()
                .user(user)
                .post(post)
                .parent(null)
                .isAnonymous(null)
                .content(correctContent)
                .build();
          }
      ).isInstanceOf(IllegalArgumentException.class);

    }

  }

  @Nested
  @DisplayName("Happy path 중에서")
  class Descirbe_HappyPath {

    @Test
    @DisplayName("content의 길이가 0보다 크고 300이하인지")
    public void testContentLength() {
      Comment comment = Comment.builder()
          .user(user)
          .post(post)
          .parent(null)
          .isAnonymous(true)
          .content(_300BytesString)
          .build();
      assertThat(comment.getContent().length()).isGreaterThan(0).isLessThanOrEqualTo(300);
    }

    @Test
    @DisplayName("댓글 익명순서가 올바르게 들어가는지")
    public void testAnonymousSeq() {
      //given
      Comment anonymousComment1 = Comment.builder()
          .user(user)
          .post(post)
          .parent(null)
          .isAnonymous(true)
          .anonymousSeq(post.getAnonymousSeqAndAdd())
          .content(_300BytesString)
          .build();

      Comment realComment = Comment.builder()
          .user(user)
          .post(post)
          .parent(anonymousComment1)
          .isAnonymous(false)
          .anonymousSeq(null)
          .content(_300BytesString)
          .build();

      Comment anonymousComment2 = Comment.builder()
          .user(user)
          .post(post)
          .parent(null)
          .isAnonymous(true)
          .anonymousSeq(post.getAnonymousSeqAndAdd())
          .content(_300BytesString)
          .build();

      assertThat(post.getNextAnonymousSeq()).isEqualTo(3);
      assertThat(realComment.getAnonymousSeq()).isNull();
      assertThat(anonymousComment2.getAnonymousSeq()).isEqualTo(2);
    }

  }

}