package com.prgrms.coretime.post.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.coretime.S3MockConfig;
import com.prgrms.coretime.common.util.JwtService;
import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardType;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.repository.BoardRepository;
import com.prgrms.coretime.post.domain.repository.PostRepository;
import com.prgrms.coretime.post.dto.request.PostCreateRequest;
import com.prgrms.coretime.post.dto.request.PostUpdateRequest;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import io.findify.s3mock.S3Mock;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
@Import(S3MockConfig.class)
class PostControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private SchoolRepository schoolRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BoardRepository boardRepository;
  @Autowired
  private JwtService jwtService;
  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  PostRepository postRepository;
  @Autowired
  S3Mock s3Mock;

  User currentUser;
  Board board;
  String accessToken;

  @BeforeEach
  void setUp() {
    School school = schoolRepository.save(
        new School("상상대학교", "sangsang.ac.kr")
    );

    board = boardRepository.save(Board.builder()
        .name("자유게시판")
        .category(BoardType.BASIC)
        .school(school)
        .build());

    currentUser = userRepository.save(LocalUser.builder()
        .nickname("테스트")
        .email("test@sangsang.ac.kr")
        .name("김테스트")
        .school(school)
        .password("1q2w3e")
        .build());

    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
    accessToken = jwtService.createAccessToken(
        currentUser.getId(),
        currentUser.getSchool().getId(),
        currentUser.getNickname(),
        currentUser.getEmail(),
        authorities
    );
  }

  @Test
  @DisplayName("게시판 별 게시글 조회 api 테스트")
  public void testShowPostsByBoard() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/boards/{boardId}/posts", board.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("게시판 별 게시글 검색 api 테스트")
  public void testSearchPostsAtBoard() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/boards/{boardId}/posts/search", board.getId())
            .header("accessToken", accessToken)
            .param("keyword", "테스트")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("게시판 별 게시글 검색 api 키워드 없이 검색 테스트")
  public void testSearchPostsAtBoardWithNoKeyword() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/boards/{boardId}/posts/search", board.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("게시글 생성 api 테스트")
  public void testCreatePost() throws Exception {
    //Given
    List<MockMultipartFile> photos = List.of(
        new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
        new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes())
    );

    //When //Then
    mockMvc.perform(multipart("/api/v1/boards/{boardId}/posts", board.getId())
            .file("photos", photos.get(0).getBytes())
            .file("photos", photos.get(1).getBytes())
            .param("title", "제목")
            .param("content", "내용")
            .param("isAnonymous", "true")
            .header("accessToken", accessToken)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  @DisplayName("빈 제목의 게시글 생성 api 테스트")
  public void testCreatePostWithBlankTitle() throws Exception {
    //Given
    PostCreateRequest createRequest = PostCreateRequest.builder()
        .content("내용")
        .title("   ")
        .isAnonymous(true)
        .build();

    //When //Then
    mockMvc.perform(post("/api/v1/boards/{boardId}/posts", board.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("핫 게시판 게시글 조회 api 테스트")
  public void testShowHotPosts() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/posts/hot")
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("베스트 게시판 게시글 조회 api 테스트")
  public void testShowBestPosts() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/posts/best")
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("내 게시글 조회 api 테스트")
  public void testShowMyPosts() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/posts/my")
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 단 글 조회 api 테스트")
  public void testShowMyCommentedPosts() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/posts/mycomment")
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("게시글 상세 조회 api 테스트")
  public void testShowPost() throws Exception {
    //Given
    Post post = postRepository.save(Post.builder()
        .user(currentUser)
        .content("내용")
        .title("제목")
        .isAnonymous(true)
        .board(board)
        .build());

    //When //Then
    mockMvc.perform(get("/api/v1/posts/{postId}", post.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("잘못된 게시글 상세 조회 api 테스트")
  public void testShowInvalidPost() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/posts/{postId}", 9999)
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("게시글 수정 api 테스트")
  public void testUpdatePost() throws Exception {
    //Given
    Post post = postRepository.save(Post.builder()
        .user(currentUser)
        .content("내용")
        .title("제목")
        .isAnonymous(true)
        .board(board)
        .build());
    PostUpdateRequest updateRequest = new PostUpdateRequest("변경된 제목", "변경된 내용");

    //When //Then
    mockMvc.perform(patch("/api/v1/posts/{postId}", post.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("빈 제목의 게시글 수정 api 테스트")
  public void testUpdatePostWithBlankTitle() throws Exception {
    //Given
    Post post = postRepository.save(Post.builder()
        .user(currentUser)
        .content("내용")
        .title("제목")
        .isAnonymous(true)
        .board(board)
        .build());
    PostUpdateRequest updateRequest = new PostUpdateRequest("   ", "변경된 내용");

    //When //Then
    mockMvc.perform(patch("/api/v1/posts/{postId}", post.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("게시글 삭제 api 테스트")
  public void testDeletePost() throws Exception {
    //Given
    Post post = postRepository.save(Post.builder()
        .user(currentUser)
        .content("내용")
        .title("제목")
        .isAnonymous(true)
        .board(board)
        .build());

    //When //Then
    mockMvc.perform(delete("/api/v1/posts/{postId}", post.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("전체 게시글 검색 api 테스트")
  public void testSearchPost() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/posts")
            .header("accessToken", accessToken)
            .param("keyword", "테스트")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("전체 게시글 검색 api 키워드 없이 검색 테스트")
  public void testSearchPostWithNoKeyword() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/posts")
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("게시글 좋아요 및 좋아요 취소 api 테스트")
  public void testLikeAndUnlikePost() throws Exception {
    //Given
    Post post = postRepository.save(Post.builder()
        .user(currentUser)
        .content("내용")
        .title("제목")
        .isAnonymous(true)
        .board(board)
        .build());

    //When //Then
    mockMvc.perform(post("/api/v1/posts/{postId}/like", post.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
    mockMvc.perform(delete("/api/v1/posts/{postId}/like", post.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("게시글 중복 좋아요 api 테스트")
  public void testDuplicatedLikePost() throws Exception {
    //Given
    Post post = postRepository.save(Post.builder()
        .user(currentUser)
        .content("내용")
        .title("제목")
        .isAnonymous(true)
        .board(board)
        .build());

    //When //Then
    mockMvc.perform(post("/api/v1/posts/{postId}/like", post.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
    mockMvc.perform(post("/api/v1/posts/{postId}/like", post.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("게시글 잘못된 좋아요 취소 api 테스트")
  public void testInvalidUnlikePost() throws Exception {
    //Given
    Post post = postRepository.save(Post.builder()
        .user(currentUser)
        .content("내용")
        .title("제목")
        .isAnonymous(true)
        .board(board)
        .build());

    //When //Then
    mockMvc.perform(delete("/api/v1/posts/{postId}/like", post.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }
}