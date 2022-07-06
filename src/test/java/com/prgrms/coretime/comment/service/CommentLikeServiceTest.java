package com.prgrms.coretime.comment.service;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.comment.domain.CommentLike;
import com.prgrms.coretime.comment.domain.repository.CommentLikeRepository;
import com.prgrms.coretime.comment.domain.repository.CommentRepository;
import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardType;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.repository.PostRepository;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PostRepository postRepository;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private CommentLikeRepository commentLikeRepository;

  @InjectMocks
  private CommentLikeService commentLikeService;

  private String localTestEmail = "local@university.ac.kr";
  School school = new School("university", "university@university.ac.kr");

  private Board board = Board.builder()
      .category(BoardType.BASIC)
      .name("게시판")
      .school(school)
      .build();
  ;

  private User user = LocalUser.builder()
      .nickname("local유저")
      .profileImage("예시 링크")
      .email(localTestEmail)
      .name("김승은로컬")
      .school(school)
      .password("test1234!")
      .build();

  private Post post = Post.builder()
      .title("아 테스트 세팅하는데 손아파 죽겠다")
      .content("ㅈㄱㄴ")
      .isAnonymous(true)
      .user(user)
      .board(board)
      .build();

  private Comment comment = Comment.builder()
      .user(user)
      .post(post)
      .parent(null)
      .anonymousSeq(post.getAnonymousSeqAndAdd())
      .isAnonymous(true)
      .content("아 손목아파")
      .build();

  private CommentLike commentLike = new CommentLike(user, comment);


  @Nested
  @DisplayName("createLike : 좋아요를 생성할 때")
  class Describe_CreateLike {

    Long userId = 1L;
    Long commentId = 1L;

    @Test
    @DisplayName("사용자가 이미 좋아요를 눌렀을 때")
    public void testAlreadyLike() {

    }

    @Test
    @DisplayName("정상적으로 좋아요를 생성할 때")
    public void testCorrectRequest() {

    }
  }

  @Nested
  @DisplayName("deleteLike : 좋아요를 삭제 할 때")
  class Descrbe_DeleteLike {

    Long userId = 1L;
    Long commentId = 1L;

    @Test
    @DisplayName("사용자가 이미 좋아요를 누르지 않았을때")
    public void testNotPushLike() {

    }

    @Test
    @DisplayName("정상적으로 좋아요를 지웠을 때")
    public void testCorrectRequest() {

    }

  }


}