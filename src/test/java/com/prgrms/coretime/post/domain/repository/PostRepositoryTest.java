package com.prgrms.coretime.post.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.comment.domain.repository.CommentRepository;
import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardType;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@DataJpaTest
class PostRepositoryTest {

  @Autowired
  private PostRepository postRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private BoardRepository boardRepository;
  @Autowired
  private SchoolRepository schoolRepository;
  @Autowired
  private EntityManager em;

  PageRequest defaultPageRequest = PageRequest.of(0, 10, Sort.by(Direction.DESC, "created_at"));
  School school;
  Board board1;
  Board board2;
  User user1;
  User user2;
  User user3;

  @BeforeEach()
  void setUp() {
    school = schoolRepository.save(
        new School("상상대학교", "sangsang.ac.kr")
    );

    board1 = boardRepository.save(Board.builder()
        .name("자유게시판")
        .category(BoardType.BASIC)
        .school(school)
        .build());

    board2 = boardRepository.save(Board.builder()
        .name("안자유게시판")
        .category(BoardType.BASIC)
        .school(school)
        .build());

    user1 = userRepository.save(LocalUser.builder()
        .nickname("테스트1")
        .email("test1@test1")
        .name("테식이1")
        .school(school)
        .password("1q2w3e")
        .build());

    user2 = userRepository.save(LocalUser.builder()
        .nickname("테스트2")
        .email("test2@test2")
        .name("테식이2")
        .school(school)
        .password("1q2w3e")
        .build());

    user3 = userRepository.save(LocalUser.builder()
        .nickname("테스트3")
        .email("test3@test3")
        .name("테식이3")
        .school(school)
        .password("1q2w3e")
        .build());
  }

  @BeforeEach
  void clear() {
    commentRepository.deleteAll();
    postRepository.deleteAll();
    boardRepository.deleteAll();
    userRepository.deleteAll();
    userRepository.deleteAll();
  }


  @Test
  @DisplayName("게시글 상세 조회 테스트")
  public void testCreatePost() {
    //Given
    Post post = postRepository.save(Post.builder()
        .board(board1)
        .content("내용")
        .isAnonymous(true)
        .title("제목")
        .user(user1)
        .build()
    );
    Comment comment1 = commentRepository.save(Comment.builder()
        .post(post)
        .content("댓글")
        .isAnonymous(true)
        .user(user2)
        .build());
    Comment comment2 = commentRepository.save(Comment.builder()
        .post(post)
        .content("댓글")
        .isAnonymous(true)
        .user(user3)
        .build());
    em.flush();
    em.clear();

    //When
    Optional<Post> foundPost = postRepository.findPostById(post.getId());

    //Then
    assertThat(foundPost.isPresent()).isTrue();
    assertThat(foundPost.get().getComments()).hasSize(2);
  }
}