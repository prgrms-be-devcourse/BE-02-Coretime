package com.prgrms.coretime.post.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.comment.domain.repository.CommentRepository;
import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardType;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.PostLike;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
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
  private PostLikeRepository postLikeRepository;
  @Autowired
  private EntityManager em;

  PageRequest defaultPageRequest = PageRequest.of(0, 10, Sort.by(Direction.DESC, "createdAt"));
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
    postLikeRepository.deleteAll();
    commentRepository.deleteAll();
    postRepository.deleteAll();
    boardRepository.deleteAll();
    userRepository.deleteAll();
    userRepository.deleteAll();
  }


  @Test
  @DisplayName("게시글 상세 조회 테스트")
  public void testFindPost() {
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

  @Test
  @DisplayName("게시글 댓글 조회 테스트")
  public void testFindCommentByPost() {
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
    Page<Comment> comments = postRepository.findCommentsByPost(post.getId(), defaultPageRequest);

    //Then
    assertThat(comments.getContent()).hasSize(2);
  }

  @Test
  @DisplayName("게시판 별 게시글 목록 조회 테스트")
  public void testFindPostsByBoard() {
    //Given
    for (int i = 0; i < 17; i++) {
      postRepository.save(Post.builder()
          .board(board1)
          .content("내용")
          .isAnonymous(true)
          .title("제목")
          .user(user1)
          .build()
      );
    }
    for (int i = 0; i < 18; i++) {
      postRepository.save(Post.builder()
          .board(board2)
          .content("내용")
          .isAnonymous(true)
          .title("제목")
          .user(user1)
          .build()
      );
    }
    em.flush();
    em.clear();

    //When
    PageRequest nextPageRequest = PageRequest.of(1, 10, Sort.by(Direction.DESC, "createdAt"));
    Page<Post> board1Posts0 = postRepository.findPostsByBoardId(board1.getId(), defaultPageRequest);
    Page<Post> board1Posts1 = postRepository.findPostsByBoardId(board1.getId(), nextPageRequest);
    Page<Post> board2Posts0 = postRepository.findPostsByBoardId(board2.getId(), defaultPageRequest);
    Page<Post> board2Posts1 = postRepository.findPostsByBoardId(board2.getId(), nextPageRequest);

    //Then
    assertThat(board1Posts0.getContent()).hasSize(10);
    assertThat(board1Posts1.getContent()).hasSize(7);
    assertThat(board2Posts0.getContent()).hasSize(10);
    assertThat(board2Posts1.getContent()).hasSize(8);
  }

  @Test
  @DisplayName("유저 별 게시글 목록 조회 테스트")
  public void testFindPostsByUser() {
    //Given
    for (int i = 0; i < 17; i++) {
      postRepository.save(Post.builder()
          .board(board1)
          .content("내용")
          .isAnonymous(true)
          .title("제목")
          .user(user1)
          .build()
      );
    }
    for (int i = 0; i < 18; i++) {
      postRepository.save(Post.builder()
          .board(board1)
          .content("내용")
          .isAnonymous(true)
          .title("제목")
          .user(user2)
          .build()
      );
    }
    em.flush();
    em.clear();

    //When
    PageRequest nextPageRequest = PageRequest.of(1, 10, Sort.by(Direction.DESC, "createdAt"));
    Page<Post> board1Posts0 = postRepository.findPostsByUserId(user1.getId(), defaultPageRequest);
    Page<Post> board1Posts1 = postRepository.findPostsByUserId(user1.getId(), nextPageRequest);
    Page<Post> board2Posts0 = postRepository.findPostsByUserId(user2.getId(), defaultPageRequest);
    Page<Post> board2Posts1 = postRepository.findPostsByUserId(user2.getId(), nextPageRequest);

    //Then
    assertThat(board1Posts0.getContent()).hasSize(10);
    assertThat(board1Posts1.getContent()).hasSize(7);
    assertThat(board2Posts0.getContent()).hasSize(10);
    assertThat(board2Posts1.getContent()).hasSize(8);
  }

  @Test
  @DisplayName("유저가 댓글 단 게시글 목록 조회 테스트")
  public void testFindPostThatUserCommentedAt() {
    //Given
    for (int i = 0; i < 17; i++) {
      Post post = postRepository.save(Post.builder()
          .board(board1)
          .content("내용")
          .isAnonymous(true)
          .title("제목")
          .user(user1)
          .build()
      );
      commentRepository.save(Comment.builder()
          .post(post)
          .content("댓글")
          .isAnonymous(true)
          .user(user2)
          .build());
    }
    for (int i = 0; i < 18; i++) {
      Post post = postRepository.save(Post.builder()
          .board(board1)
          .content("내용")
          .isAnonymous(true)
          .title("제목")
          .user(user2)
          .build()
      );
      commentRepository.save(Comment.builder()
          .post(post)
          .content("댓글")
          .isAnonymous(true)
          .user(user1)
          .build());
    }
    em.flush();
    em.clear();

    //When
    List<Long> user1PostsIds = postRepository.findPostIdsThatUserCommentedAt(user1.getId());
    List<Long> user2PostsIds = postRepository.findPostIdsThatUserCommentedAt(user2.getId());
    PageRequest nextPageRequest = PageRequest.of(1, 10, Sort.by(Direction.DESC, "createdAt"));
    Page<Post> board1Posts0 = postRepository.findPostsThatUserCommentedAt(user1PostsIds, defaultPageRequest);
    Page<Post> board1Posts1 = postRepository.findPostsThatUserCommentedAt(user1PostsIds, nextPageRequest);
    Page<Post> board2Posts0 = postRepository.findPostsThatUserCommentedAt(user2PostsIds, defaultPageRequest);
    Page<Post> board2Posts1 = postRepository.findPostsThatUserCommentedAt(user2PostsIds, nextPageRequest);

    //Then
    assertThat(board1Posts0.getContent()).hasSize(10);
    assertThat(board1Posts1.getContent()).hasSize(8);
    assertThat(board2Posts0.getContent()).hasSize(10);
    assertThat(board2Posts1.getContent()).hasSize(7);
  }

  @Test
  @DisplayName("게시글 검색 테스트")
  public void testSearchPost() {
    //Given
    for (int i = 0; i < 3; i++) {
      postRepository.save(Post.builder()
          .board(board1)
          .content("내용" + i + "내용")
          .isAnonymous(true)
          .title("제목")
          .user(user1)
          .build()
      );
    }
    for (int i = 2; i < 3; i++) {
      postRepository.save(Post.builder()
          .board(board1)
          .content("내용")
          .isAnonymous(true)
          .title("제목" + i + "제목")
          .user(user2)
          .build()
      );
    }
    em.flush();
    em.clear();

    //When
    Page<Post> searchedPosts1 = postRepository.searchPosts("1", defaultPageRequest);
    Page<Post> searchedPosts2 = postRepository.searchPosts("2", defaultPageRequest);
    Page<Post> searchedPosts3 = postRepository.searchPosts("3", defaultPageRequest);

    //Then
    assertThat(searchedPosts1.getContent()).hasSize(1);
    assertThat(searchedPosts2.getContent()).hasSize(2);
    assertThat(searchedPosts3.getContent()).hasSize(0);
  }

  @Test
  @DisplayName("게시판 별 게시글 검색 테스트")
  public void testSearchPostAtBoard() {
    //Given
    for (int i = 0; i < 7; i++) {
      postRepository.save(Post.builder()
          .board(board1)
          .content("내용" + i + "내용")
          .isAnonymous(true)
          .title("제목")
          .user(user1)
          .build()
      );
    }
    for (int i = 0; i < 8; i++) {
      postRepository.save(Post.builder()
          .board(board2)
          .content("내용")
          .isAnonymous(true)
          .title("제목" + i + "제목")
          .user(user2)
          .build()
      );
    }
    em.flush();
    em.clear();

    //When
    Page<Post> searchedPostsAtBoard1 = postRepository.searchPostsAtBoard("7", board1.getId(), defaultPageRequest);
    Page<Post> searchedPostsAtBoard2 = postRepository.searchPostsAtBoard("7", board2.getId(), defaultPageRequest);

    //Then
    assertThat(searchedPostsAtBoard1.getContent()).hasSize(0);
    assertThat(searchedPostsAtBoard2.getContent()).hasSize(1);
  }

  @Test
  @DisplayName("게시글 좋아요 기준 목록 조회 테스트")
  public void testFindPostsByLikeCount() {
    //Given
    for (int i = 0; i < 3; i++) {
      Post post = postRepository.save(Post.builder()
          .board(board1)
          .content("내용" + i + "내용")
          .isAnonymous(true)
          .title("제목")
          .user(user1)
          .build()
      );
      postLikeRepository.save(new PostLike(post, user2));
    }
    for (int i = 0; i < 5; i++) {
      Post post = postRepository.save(Post.builder()
          .board(board2)
          .content("내용")
          .isAnonymous(true)
          .title("제목" + i + "제목")
          .user(user2)
          .build()
      );
      postLikeRepository.save(new PostLike(post, user1));
      postLikeRepository.save(new PostLike(post, user3));
    }
    em.flush();
    em.clear();

    //When
    Page<Post> liked1Posts = postRepository.findPostsByLikeCount(1, defaultPageRequest);
    Page<Post> liked2Posts = postRepository.findPostsByLikeCount(2, defaultPageRequest);

    //Then
    assertThat(liked1Posts.getContent()).hasSize(8);
    assertThat(liked2Posts.getContent()).hasSize(5);
  }
}