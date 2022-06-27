package com.prgrms.coretime.comment.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.user.domain.User;
import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class CommentTest {

  private User user;

  private Post post;

  private String content;

  String _300BytesString;

  String _301BytesString;

  @BeforeAll
  void setup() {
    user = new User("example@email.com", "example");
    post = Post.builder()
        .title("title")
        .content("게시글 내용입니다.")
        .isAnonymous(true)
        .build();

    content = "이것은 공백도 아니며, 300바이트를 넘지 않아요!";

    // euc-kr 인코딩으로 300바이트
    _300BytesString = "이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어."
        + "이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어."
        + "이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어."
        + "이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어."
        + "이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어.이거는300길이가안넘어..";

    // euc-kr 인코딩으로 301바이트
    _301BytesString = "이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어."
        + "이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어."
        + "이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어."
        + "이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어.이거는300길이가넘어."
        + "이거는300길이가넘어.이거는300길이가넘어..";

  }

  @Test
  @DisplayName("user가 null일 경우")
  public void testUserNull() {
    assertThatThrownBy(() -> {
          Comment.builder()
              .user(null)
              .post(post)
              .parent(null)
              .isAnonymous(true)
              .content(content)
              .build();
        }
    ).isInstanceOf(IllegalArgumentException.class);
  }

  /**
   * TODO : 인증 구현 완료 후 추후 검증
   */
  @Test
  @DisplayName("user가 현재 로그인한 유저가 맞는지")
  public void testUserIsCurrentUser() {
  }

  @Test
  @DisplayName("post가 null일 경우")
  public void testPostNull() {
    assertThatThrownBy(() -> {
          Comment.builder()
              .user(user)
              .post(null)
              .parent(null)
              .isAnonymous(true)
              .content(content)
              .build();
        }
    ).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("content가 null 일경우")
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
  @DisplayName("content가 공백일 경우")
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
  @DisplayName("content가 300바이트 넘길 경우")
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
  @DisplayName("post, user isAnonymous가 null이 아니며 content가 공백이 아니면서 300바이트 이내일때 생성 성공")
  public void happyPath() throws UnsupportedEncodingException {

    Comment comment = Comment.builder()
        .user(user)
        .post(post)
        .parent(null)
        .isAnonymous(true)
        .content(_300BytesString)
        .build();

    assertThat(comment.getContent().length()).isEqualTo(300);
  }
}