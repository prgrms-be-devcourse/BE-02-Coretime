package com.prgrms.coretime.comment.domain.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.prgrms.coretime.TestConfig;
import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.comment.domain.CommentLike;
import com.prgrms.coretime.comment.domain.CommentLikeId;
import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardType;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.repository.BoardRepository;
import com.prgrms.coretime.post.domain.repository.PostRepository;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.OAuthUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Rollback(false) // query 확인하기 위해서 추후 지우겠습니당.
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestInstance(Lifecycle.PER_CLASS)
class CommentLikeRepositoryTest {

  @PersistenceContext
  EntityManager em;

  @Autowired
  private SchoolRepository schoolRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private CommentLikeRepository commentLikeRepository;

  private School school;

  private User localUser;

  private User oauthUser;

  private Board board;

  private Post anonyPost;

  private Comment comment;

  private CommentLike commentLike;

  void setSchool() {
    school = new School("아주대학교", "ajou.ac.kr");
    school = schoolRepository.save(school);
  }

  void setUser() {
    String localTestEmail = "local@university.ac.kr";
    String oauthTestEmail = "oauth@ajou.ac.kr";

    localUser = LocalUser.builder()
        .nickname("local유저")
        .profileImage("예시 링크")
        .email(localTestEmail)
        .name("김승은로컬")
        .school(school)
        .password("test1234!")
        .build();

    oauthUser = OAuthUser.builder()
        .nickname("oauth유저")
        .profileImage("예시 링크")
        .email(oauthTestEmail)
        .name("김승은oauth")
        .school(school)
        .provider("카카오")
        .providerId("카카오id")
        .build();

    localUser = userRepository.save(localUser);
    oauthUser = userRepository.save(oauthUser);
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
        .user(localUser)
        .board(board)
        .build();
    anonyPost = postRepository.save(anonyPost);
  }

  void setComment() {
    comment = Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(null)
        .isAnonymous(true)
        .content("응 근데 테스트 안짜면 니 망해~")
        .build();

    comment = commentRepository.save(comment);
  }

  void setCommentLike() {
    commentLike = new CommentLike(localUser, comment);
    commentLike = commentLikeRepository.save(commentLike);
  }

  @BeforeAll
  void setup() {
    setSchool();
    setUser();
    setBoard();
    setPost();
    setComment();
    setCommentLike();
  }

  @Test
  @DisplayName("좋아요가 존재하는 지 검증")
  public void testExist() {
    CommentLikeId id = new CommentLikeId(localUser.getId(), comment.getId());
    assertThat(commentLikeRepository.existsById(id)).isTrue();
  }

  @Test
  @DisplayName("좋아요가 존재 안하는 지 검증")
  void testNotExist() {
    CommentLikeId id = new CommentLikeId(localUser.getId(), 0L);
    assertThat(commentLikeRepository.existsById(id)).isFalse();
  }

  @Test
  @DisplayName("좋아요가 삭제 되는지")
  void testDelete() {
    CommentLikeId id = new CommentLikeId(localUser.getId(), comment.getId());
    commentLikeRepository.deleteById(id);

    em.flush();
    em.clear();

    assertThat(commentLikeRepository.existsById(id)).isFalse();
  }

  @Test
  @DisplayName("댓글과 좋아요 양방향 연관관계 매핑테스트")
  void testCommentAndCommentLike() {

    User user = userRepository.findById(oauthUser.getId())
        .orElseThrow(IllegalArgumentException::new);

    CommentLike commentLike2 = new CommentLike(user, comment);
    CommentLike savedLike = commentLikeRepository.save(commentLike2);

    em.flush();
    em.clear();

    Comment calledComment = commentRepository.findById(comment.getId())
        .orElseThrow(IllegalArgumentException::new);

    CommentLike calledLike = commentLikeRepository.findById(commentLike2.getId())
        .orElseThrow(IllegalAccessError::new);

    assertThat(calledComment.getLikes().size()).isEqualTo(2);
    assertThat(calledLike.getComment()).isEqualTo(calledComment);
  }

}