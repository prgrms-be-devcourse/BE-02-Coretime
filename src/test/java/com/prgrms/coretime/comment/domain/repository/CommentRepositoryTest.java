package com.prgrms.coretime.comment.domain.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.prgrms.coretime.TestConfig;
import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardRepository;
import com.prgrms.coretime.post.domain.BoardType;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.PostRepository;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional
@Rollback(false) // query 확인하기 위해서
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestInstance(Lifecycle.PER_CLASS)
class CommentRepositoryTest {

  @PersistenceContext
  EntityManager em;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private SchoolRepository schoolRepository;

  private User user;

  private Board board;

  private Post anonyPost;

  private Comment parent;

  private School school;

  void setSchool() {
    school = new School("university", "university@university.ac.kr");
    school = schoolRepository.save(school);
  }

  void setUser() {
    String localTestEmail = "local@university.ac.kr";
    user = LocalUser.builder()
        .nickname("local_user")
        .profileImage("sample link")
        .email(localTestEmail)
        .name("student_local")
        .school(school)
        .password("test1234!")
        .build();
    user = userRepository.save(user);
  }

  void setBoard() {
    board = Board.builder()
        .category(BoardType.BASIC)
        .name("게시판")
        .school(school)
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
    anonyPost = postRepository.save(anonyPost);
  }

  void setComment() {
    parent = Comment.builder()
        .user(user)
        .post(anonyPost)
        .parent(null)
        .isAnonymous(true)
        .content("응 근데 테스트 안짜면 니 망해~")
        .build();

    parent = commentRepository.save(parent);
  }

  @BeforeAll
  void setup() {
    setUser();
    setBoard();
    setPost();
    setComment();
  }

  @Nested
  @DisplayName("댓글을 저장할 때 ")
  class Describe_Save {

    @Test
    @DisplayName("부모 댓글과 자식 댓글이 양방향으로 연결 되어 있는지")
    public void testParentChild() {
      Comment child = Comment.builder()
          .user(user)
          .post(anonyPost)
          .parent(parent)
          .isAnonymous(true)
          .content("나는 자식댓글")
          .build();

      Comment savedChild = commentRepository.save(child);

      em.flush();
      em.clear();

      Comment calledChild = commentRepository.findById(savedChild.getId()).get();
      Comment calledParent = commentRepository.findById(parent.getId()).get();

      assertThat(calledChild.getParent()).isEqualTo(calledParent);
    }


  }


}