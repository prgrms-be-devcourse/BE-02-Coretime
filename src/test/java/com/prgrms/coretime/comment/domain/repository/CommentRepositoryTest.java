package com.prgrms.coretime.comment.domain.repository;

import com.prgrms.coretime.TestConfig;
import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardRepository;
import com.prgrms.coretime.post.domain.BoardType;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.PostRepository;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class CommentRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private CommentRepository commentRepository;

  private User user;

  private Board board;

  private Post anonyPost;

  private Comment comment;

  School testSchool = new School("university", "university@university.ac.kr");

  void setUser() {
    String localTestEmail = "local@university.ac.kr";
    user = LocalUser.builder()
        .nickname("local_user")
        .profileImage("sample link")
        .email(localTestEmail)
        .name("student_local")
        .school(testSchool)
        .password("test1234!")
        .build();
    user = userRepository.save(user);
  }

  void setBoard() {
    board = Board.builder()
        .category(BoardType.BASIC)
        .name("게시판")
        .school(testSchool)
        .build();
    board = boardRepository.save(board);
  }

  /**
   * 주의: 현재 user와 board가 db에 있는 인스턴스일 수 잇는건 순서를 엄격하게 맞춘 탓임.
   */
  void setPost() {
    anonyPost = Post.builder()
        .title("아 테스트 세팅하는데 손아파 죽겠다")
        .content("ㅈㄱㄴ")
        .isAnonymous(true)
        .user(user)
        .board(board)
        .build();
  }

  void setComment() {
    comment = Comment.builder()
        .user(user)
        .post(anonyPost)
        .parent(null)
        .isAnonymous(true)
        .content("응 근데 테스트 안짜면 니 망해~")
        .build();
  }

  @BeforeEach
  void setup() {
    setUser();
    setBoard();
    setPost();
    setComment();
  }

  @AfterEach
  void tearDown() {
    userRepository.delete(user);
    boardRepository.delete(board);
    postRepository.delete(anonyPost);
    commentRepository.delete(comment);
  }

  @Nested
  @DisplayName("댓글을 저장할 때 ")
  class Describe_Save {

    @Nested
    @DisplayName("Happy Path 테스트 중에서 ")
    class Describe_HappyPath {

      @Test
      @DisplayName("값이 올바르게 저장되는지")
      public void test() {
        System.out.println(user.getId());
        System.out.println(board.getId());
        System.out.println(anonyPost.getId());
        System.out.println(comment.getId());
      }
    }
  }


}