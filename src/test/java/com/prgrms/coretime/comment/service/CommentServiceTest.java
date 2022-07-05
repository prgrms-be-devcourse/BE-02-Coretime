package com.prgrms.coretime.comment.service;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.comment.domain.repository.CommentRepository;
import com.prgrms.coretime.comment.dto.request.CommentCreateRequest;
import com.prgrms.coretime.comment.dto.response.CommentsOnPostResponse;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardType;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.repository.PostRepository;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PostRepository postRepository;

  @Mock
  private CommentRepository commentRepository;

  @InjectMocks
  private CommentService commentService;

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

  @Nested
  @DisplayName("createComment : 댓글 생성 중에서 ")
  class Decribe_CreateComment {

    CommentCreateRequest commentCreateRequest = CommentCreateRequest.builder()
        .postId(1L)
        .parentId(null)
        .content("요청입니다.")
        .isCommentAnonymous(true)
        .build();

    Long userId = 1L;
    Long postId = commentCreateRequest.getPostId();

    @Test
    @DisplayName("user가 없을 경우")
    public void testUserNotFound() {
      given(userRepository.findById(userId)).willThrow(NotFoundException.class);

      assertThatThrownBy(() -> commentService.createComment(userId, any()))
          .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("post가 없을 경우")
    public void testPostNotFound() {
      given(userRepository.findById(any())).willReturn(of(user));
      given(postRepository.findById(postId)).willThrow(NotFoundException.class);

      assertThatThrownBy(() -> commentService.createComment(any(), commentCreateRequest))
          .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("정상적인 요청이 들어왔을 경우")
    public void testCorrectRequest() {
      given(userRepository.findById(userId)).willReturn(of(user));
      given(postRepository.findById(postId)).willReturn(of(post));

      commentService.createComment(userId, commentCreateRequest);

      verify(commentRepository).save(any());
    }
  }

  @Nested
  @DisplayName("deleteComment : 댓글 삭제 중에서")
  class Describe_DeleteComment {

    Long commentId;
    Long userId = 1L;

    @Test
    @DisplayName("삭제 할 Comment가 존재 하지 않을 경우")
    public void testCommentNotFound() {
      given(commentRepository.findById(commentId)).willThrow(NotFoundException.class);

      assertThatThrownBy(() -> commentService.deleteComment(any(), commentId))
          .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("정상적인 요청일 경우")
    public void testCorrectRequest() {
      given(commentRepository.findById(commentId)).willReturn(of(comment));

      commentService.deleteComment(userId, commentId);

      verify(commentRepository).findById(commentId);
    }
  }

  @Nested
  @DisplayName("searchCommentsByPost : 댓글 조회 중에서")
  class Describe_SearchComments {

    Long postId = 1L;
    List<CommentsOnPostResponse> comments = List.of(
        new CommentsOnPostResponse(
            1L, 1L, 1L, 2, "익명1", "응답"
        )
    );
    PageRequest pageRequest = PageRequest.of(0, 20);
    Page<CommentsOnPostResponse> pageResponse
        = new PageImpl<>(comments, pageRequest, 15);

    @Test
    @DisplayName("Post가 없을 경우")
    public void testPostNotFound() {
      given(postRepository.existsById(postId)).willThrow(NotFoundException.class);

      assertThatThrownBy(() -> commentService.searchCommentsByPost(postId, any()))
          .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("정상 요청일 경우")
    public void testCorrectRequest() {
      given(commentRepository.findByPost(postId, pageRequest)).willReturn(pageResponse);

      commentService.searchCommentsByPost(postId, pageRequest);

      verify(commentRepository).findByPost(postId, pageRequest);
    }

  }

}