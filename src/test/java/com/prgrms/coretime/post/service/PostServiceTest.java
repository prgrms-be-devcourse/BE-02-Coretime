package com.prgrms.coretime.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.amazonaws.services.s3.AmazonS3Client;
import com.prgrms.coretime.S3MockConfig;
import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.comment.domain.repository.CommentRepository;
import com.prgrms.coretime.common.ErrorCode;
import com.prgrms.coretime.common.error.exception.AlreadyExistsException;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardType;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.repository.BoardRepository;
import com.prgrms.coretime.post.domain.repository.PostRepository;
import com.prgrms.coretime.post.dto.request.PostCreateRequest;
import com.prgrms.coretime.post.dto.request.PostUpdateRequest;
import com.prgrms.coretime.post.dto.response.PostIdResponse;
import com.prgrms.coretime.post.dto.response.PostResponse;
import com.prgrms.coretime.post.dto.response.PostSimpleResponse;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@SpringBootTest
@Import(S3MockConfig.class)
@ActiveProfiles("test")
class PostServiceTest {

  @Autowired
  private PostService postService;
  @Autowired
  private PostRepository postRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BoardRepository boardRepository;
  @Autowired
  private SchoolRepository schoolRepository;
  @Autowired
  private CommentRepository commentRepository;
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
        .nickname("일식이")
        .email("test1@sangsang.ac.kr")
        .name("일식이")
        .school(school)
        .password("1q2w3e")
        .build());

    user2 = userRepository.save(LocalUser.builder()
        .nickname("이식이")
        .email("test2@sangsang.ac.kr")
        .name("이식이")
        .school(school)
        .password("1q2w3e")
        .build());

    user3 = userRepository.save(LocalUser.builder()
        .nickname("삼식이")
        .email("test3@sangsang.ac.kr")
        .name("삼식이")
        .school(school)
        .password("1q2w3e")
        .build());
  }

  @Test
  @DisplayName("게시글 생성 및 목록 조회 테스트")
  public void testCreatePostAndGetPosts() {
    //Given
    PostCreateRequest createRequest = PostCreateRequest.builder()
        .content("내용")
        .title("제목")
        .isAnonymous(true)
        .build();
    for (int i = 0; i < 9; i++) {
      if (i % 2 == 0) {
        postService.createPost(board1.getId(), user1.getId(), createRequest);
      } else {
        postService.createPost(board2.getId(), user2.getId(), createRequest);
      }
    }

    //When
    Page<PostSimpleResponse> board1posts = postService.getPostsByBoard(board1.getId(),
        defaultPageRequest);
    Page<PostSimpleResponse> board2posts = postService.getPostsByBoard(board2.getId(),
        defaultPageRequest);
    Page<PostSimpleResponse> user1posts = postService.getPostsByUser(user1.getId(),
        defaultPageRequest);
    Page<PostSimpleResponse> user2posts = postService.getPostsByUser(user2.getId(),
        defaultPageRequest);

    //Then
    assertThat(board1posts.getContent()).hasSize(5);
    assertThat(board2posts.getContent()).hasSize(4);
    assertThat(user1posts.getContent()).hasSize(5);
    assertThat(user2posts.getContent()).hasSize(4);
  }

  @Test
  @DisplayName("잘못된 게시판 게시글 생성 테스트")
  public void testCreatePostsUsingInvalidBoard() {
    //Given
    PostCreateRequest createRequest = PostCreateRequest.builder()
        .content("내용")
        .title("제목")
        .isAnonymous(true)
        .build();

    //When //Then
    assertThatThrownBy(() -> postService.createPost(9999L, user1.getId(), createRequest))
        .isInstanceOf(NotFoundException.class)
        .hasMessage(ErrorCode.BOARD_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("잘못된 유저 게시글 생성 테스트")
  public void testCreatePostsUsingInvalidUser() {
    //Given
    PostCreateRequest createRequest = PostCreateRequest.builder()
        .content("내용")
        .title("제목")
        .isAnonymous(true)
        .build();

    //When //Then
    assertThatThrownBy(() -> postService.createPost(board1.getId(), 9999L, createRequest))
        .isInstanceOf(NotFoundException.class)
        .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("댓글 쓴 글 리스트 조회 테스트")
  public void testGetPostsThatUserCommentedAt() {
    //Given
    for (int i = 0; i < 10; i++) {
      Post post = postRepository.save(Post.builder()
          .board(board1)
          .content("내용")
          .isAnonymous(true)
          .title("제목")
          .user(user1)
          .build()
      );
      if (i % 2 == 0) {
        commentRepository.save(Comment.builder()
            .post(post)
            .content("댓글")
            .isAnonymous(true)
            .user(user2)
            .build());
      }
    }
    em.flush();
    em.clear();

    //When
    Page<PostSimpleResponse> posts = postService.getPostsThatUserCommentedAt(user2.getId(),
        defaultPageRequest);

    //Then
    assertThat(posts.getContent()).hasSize(5);
  }

  @Test
  @DisplayName("잘못된 게시판 게시글 조회 테스트")
  public void testGetPostsByInvalidBoard() {
    //Given //When //Then
    assertThatThrownBy(() -> postService.getPostsByBoard(9999L, defaultPageRequest))
        .isInstanceOf(NotFoundException.class)
        .hasMessage(ErrorCode.BOARD_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("잘못된 유저 게시글 조회 테스트")
  public void testGetPostsByInvalidUser() {
    //Given //When //Then
    assertThatThrownBy(() -> postService.getPostsByUser(9999L, defaultPageRequest))
        .isInstanceOf(NotFoundException.class)
        .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("게시글 생성 및 상세 조회 테스트")
  public void testCreateAndGetPost(@Autowired AmazonS3Client amazonS3Client) {
    //Given
    List<MultipartFile> photos = List.of(
        new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
        new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes())
    );
    PostCreateRequest createRequest = PostCreateRequest.builder()
        .content("내용")
        .title("제목")
        .isAnonymous(true)
        .photos(photos)
        .build();
    PostIdResponse createResponse = postService.createPost(board1.getId(), user1.getId(),
        createRequest);
    Optional<Post> optionalPost = postRepository.findPostById(createResponse.postId());
    assertThat(optionalPost.isPresent()).isTrue();
    Post post = optionalPost.get();
    for (int i = 0; i < 30; i++) {
      commentRepository.save(Comment.builder()
          .post(post)
          .content("댓글")
          .isAnonymous(true)
          .user(user2)
          .build());
    }
    em.flush();
    em.clear();

    //When
    PostResponse response = postService.getPost(post.getId());

    //Then
    assertThat(response.comments()).hasSize(20);
    assertThat(response.photos()).hasSize(2);

    amazonS3Client.shutdown();
  }

  @Test
  @DisplayName("없는 게시글 상세 조회 테스트")
  public void testGetInvalidPost() {
    //Given //When //Then
    assertThatThrownBy(() -> postService.getPost(9999L))
        .isInstanceOf(NotFoundException.class)
        .hasMessage(ErrorCode.POST_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("게시글 수정 테스트")
  public void testUpdatePost() {
    //Given
    Post post = postRepository.save(Post.builder()
        .board(board1)
        .content("내용")
        .isAnonymous(true)
        .title("제목")
        .user(user1)
        .build()
    );
    em.flush();
    em.clear();

    //When
    PostUpdateRequest request = new PostUpdateRequest("변경된 제목", "변경된 내용");
    PostIdResponse updatedPostId = postService.updatePost(post.getId(), request);
    PostResponse updatedPost = postService.getPost(updatedPostId.postId());

    //Then
    assertThat(updatedPost.title()).isEqualTo(request.getTitle());
    assertThat(updatedPost.content()).isEqualTo(request.getContent());
  }

  @Test
  @DisplayName("게시글 좋아요 테스트")
  public void testLikePost() {
    //Given
    Post post = postRepository.save(Post.builder()
        .board(board1)
        .content("내용")
        .isAnonymous(true)
        .title("제목")
        .user(user1)
        .build()
    );
    em.flush();
    em.clear();

    //When
    postService.likePost(user1.getId(), post.getId());
    postService.likePost(user2.getId(), post.getId());
    postService.likePost(user3.getId(), post.getId());
    PostResponse foundPost = postService.getPost(post.getId());

    //Then
    assertThat(foundPost.likeCount()).isEqualTo(3);
  }

  @Test
  @DisplayName("게시글 중복 좋아요 테스트")
  public void testDuplicatedLikePost() {
    //Given
    Post post = postRepository.save(Post.builder()
        .board(board1)
        .content("내용")
        .isAnonymous(true)
        .title("제목")
        .user(user1)
        .build()
    );
    em.flush();
    em.clear();

    //When
    postService.likePost(user1.getId(), post.getId());
    postService.likePost(user2.getId(), post.getId());
    postService.likePost(user3.getId(), post.getId());

    //Then
    assertThatThrownBy(() -> postService.likePost(user1.getId(), post.getId()))
        .isInstanceOf(AlreadyExistsException.class)
        .hasMessage(ErrorCode.POST_LIKE_ALREADY_EXISTS.getMessage());
    assertThatThrownBy(() -> postService.likePost(user2.getId(), post.getId()))
        .isInstanceOf(AlreadyExistsException.class)
        .hasMessage(ErrorCode.POST_LIKE_ALREADY_EXISTS.getMessage());
    assertThatThrownBy(() -> postService.likePost(user3.getId(), post.getId()))
        .isInstanceOf(AlreadyExistsException.class)
        .hasMessage(ErrorCode.POST_LIKE_ALREADY_EXISTS.getMessage());
  }

  @Test
  @DisplayName("게시글 좋아요 취소 테스트")
  public void testUnlikePost() {
    //Given
    Post post = postRepository.save(Post.builder()
        .board(board1)
        .content("내용")
        .isAnonymous(true)
        .title("제목")
        .user(user1)
        .build()
    );
    em.flush();
    em.clear();

    //When
    postService.likePost(user1.getId(), post.getId());
    postService.likePost(user2.getId(), post.getId());
    postService.likePost(user3.getId(), post.getId());
    postService.unlikePost(user1.getId(), post.getId());
    postService.unlikePost(user2.getId(), post.getId());
    PostResponse foundPost = postService.getPost(post.getId());

    //Then
    assertThat(foundPost.likeCount()).isEqualTo(1);
  }

  @Test
  @DisplayName("게시글 잘못된 취소 테스트")
  public void testInvalidUnlikePost() {
    //Given
    Post post = postRepository.save(Post.builder()
        .board(board1)
        .content("내용")
        .isAnonymous(true)
        .title("제목")
        .user(user1)
        .build()
    );
    em.flush();
    em.clear();

    //When
    postService.likePost(user1.getId(), post.getId());
    postService.likePost(user2.getId(), post.getId());

    //Then
    assertThatThrownBy(() -> postService.unlikePost(user3.getId(), post.getId()))
        .isInstanceOf(NotFoundException.class)
        .hasMessage(ErrorCode.POST_LIKE_NOT_FOUND.getMessage());
  }
}