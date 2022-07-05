package com.prgrms.coretime.comment.service;

import com.prgrms.coretime.comment.domain.repository.CommentRepository;
import com.prgrms.coretime.post.domain.repository.PostRepository;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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


  @Nested
  @DisplayName("createComment : 댓글 생성 중에서 ")
  class Decribe_CreateComment {

    @Test
    @DisplayName("user가 없을 경우")
    public void testUserNotFound() {
    }

    @Test
    @DisplayName("post가 없을 경우")
    public void testPostNotFound() {

    }

    @Test
    @DisplayName("정상적인 요청이 들어왔을 경우")
    public void testCorrectRequest() {

    }

  }

  @Nested
  @DisplayName("deleteComment : 댓글 삭제 중에서")
  class Describe_DeleteComment {

    @Test
    @DisplayName("삭제 할 Comment가 존재 하지 않을 경우")
    public void testCommentNotFound() {

    }

    @Test
    @DisplayName("user가 comment를 삭제 할 때 본인 comment가 맞는지 : 권한 체크 ")
    public void testTargetUserSameCurrentUser() {

    }

    @Test
    @DisplayName("정상적인 요청일 경우")
    public void testCorrectRequest() {

    }
  }

  @Nested
  @DisplayName("searchCommentsByPost : 댓글 조회 중에서")
  class Describe_SearchComments {

    @Test
    @DisplayName("Post가 없을 경우")
    public void testPostNotFound() {

    }

    @Test
    @DisplayName("정상 요청일 경우")
    public void testCorrectRequest() {

    }

  }

}