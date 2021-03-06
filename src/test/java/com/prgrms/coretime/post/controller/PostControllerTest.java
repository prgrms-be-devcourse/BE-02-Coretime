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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
@Import(S3MockConfig.class)
@ActiveProfiles("test")
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
        new School("???????????????", "sangsang.ac.kr")
    );

    board = boardRepository.save(Board.builder()
        .name("???????????????")
        .category(BoardType.BASIC)
        .school(school)
        .build());

    currentUser = userRepository.save(LocalUser.builder()
        .nickname("?????????")
        .email("test@sangsang.ac.kr")
        .name("????????????")
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
  @DisplayName("????????? ??? ????????? ?????? api ?????????")
  public void testShowPostsByBoard() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/boards/{boardId}/posts", board.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("????????? ??? ????????? ?????? api ?????????")
  public void testSearchPostsAtBoard() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/boards/{boardId}/posts/search", board.getId())
            .header("accessToken", accessToken)
            .param("keyword", "?????????")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("????????? ??? ????????? ?????? api ????????? ?????? ?????? ?????????")
  public void testSearchPostsAtBoardWithNoKeyword() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/boards/{boardId}/posts/search", board.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("????????? ?????? api ?????????")
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
            .param("title", "??????")
            .param("content", "??????")
            .param("isAnonymous", "true")
            .header("accessToken", accessToken)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  @DisplayName("??? ????????? ????????? ?????? api ?????????")
  public void testCreatePostWithBlankTitle() throws Exception {
    //Given
    PostCreateRequest createRequest = PostCreateRequest.builder()
        .content("??????")
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
  @DisplayName("??? ????????? ????????? ?????? api ?????????")
  public void testShowHotPosts() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/posts/hot")
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("????????? ????????? ????????? ?????? api ?????????")
  public void testShowBestPosts() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/posts/best")
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("??? ????????? ?????? api ?????????")
  public void testShowMyPosts() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/posts/my")
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("?????? ??? ??? ?????? api ?????????")
  public void testShowMyCommentedPosts() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/posts/mycomment")
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("????????? ?????? ?????? api ?????????")
  public void testShowPost() throws Exception {
    //Given
    Post post = postRepository.save(Post.builder()
        .user(currentUser)
        .content("??????")
        .title("??????")
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
  @DisplayName("????????? ????????? ?????? ?????? api ?????????")
  public void testShowInvalidPost() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/posts/{postId}", 9999)
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("????????? ?????? api ?????????")
  public void testUpdatePost() throws Exception {
    //Given
    Post post = postRepository.save(Post.builder()
        .user(currentUser)
        .content("??????")
        .title("??????")
        .isAnonymous(true)
        .board(board)
        .build());
    PostUpdateRequest updateRequest = new PostUpdateRequest("????????? ??????", "????????? ??????");

    //When //Then
    mockMvc.perform(patch("/api/v1/posts/{postId}", post.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("??? ????????? ????????? ?????? api ?????????")
  public void testUpdatePostWithBlankTitle() throws Exception {
    //Given
    Post post = postRepository.save(Post.builder()
        .user(currentUser)
        .content("??????")
        .title("??????")
        .isAnonymous(true)
        .board(board)
        .build());
    PostUpdateRequest updateRequest = new PostUpdateRequest("   ", "????????? ??????");

    //When //Then
    mockMvc.perform(patch("/api/v1/posts/{postId}", post.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("????????? ?????? api ?????????")
  public void testDeletePost() throws Exception {
    //Given
    Post post = postRepository.save(Post.builder()
        .user(currentUser)
        .content("??????")
        .title("??????")
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
  @DisplayName("?????? ????????? ?????? api ?????????")
  public void testSearchPost() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/posts")
            .header("accessToken", accessToken)
            .param("keyword", "?????????")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("?????? ????????? ?????? api ????????? ?????? ?????? ?????????")
  public void testSearchPostWithNoKeyword() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/posts")
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("????????? ????????? ??? ????????? ?????? api ?????????")
  public void testLikeAndUnlikePost() throws Exception {
    //Given
    Post post = postRepository.save(Post.builder()
        .user(currentUser)
        .content("??????")
        .title("??????")
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
  @DisplayName("????????? ?????? ????????? api ?????????")
  public void testDuplicatedLikePost() throws Exception {
    //Given
    Post post = postRepository.save(Post.builder()
        .user(currentUser)
        .content("??????")
        .title("??????")
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
  @DisplayName("????????? ????????? ????????? ?????? api ?????????")
  public void testInvalidUnlikePost() throws Exception {
    //Given
    Post post = postRepository.save(Post.builder()
        .user(currentUser)
        .content("??????")
        .title("??????")
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