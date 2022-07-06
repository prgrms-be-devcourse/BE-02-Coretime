package com.prgrms.coretime.comment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.coretime.AcceptanceTest;
import com.prgrms.coretime.comment.dto.request.CommentCreateRequest;
import com.prgrms.coretime.comment.service.CommentService;
import com.prgrms.coretime.common.ErrorCode;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@DisplayName("댓글 생성 및 조회 통합/인수 테스트")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class CommentControllerTest extends AcceptanceTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private SchoolRepository schoolRepository;

  @MockBean
  private CommentService commentService;

  private String baseUrl = "/api/v1/posts/{postId}/comments";

  private String accessToken;

  private User user;

  private School school;

  @BeforeAll
  void setup() {
    school = schoolRepository.save(new School("상상대학교", "sangsang.ac.kr"));

    user = LocalUser.builder()
        .nickname("테스트")
        .email("test@sangsang.ac.kr")
        .name("김테스트")
        .school(school)
        .password("1q2w3e")
        .build();
    accessToken = getAccessToken(user);
  }


  @Nested
  @DisplayName("댓글 생성 API 실행시")
  class Describe_createCommentApi {

    Long postId = 1L;

    @Test
    @DisplayName("요청에서 postId가 null일 경우")
    public void testPostNull() throws Exception {

      CommentCreateRequest request = CommentCreateRequest
          .builder()
          .postId(null)
          .parentId(null)
          .isCommentAnonymous(true)
          .content("정상")
          .build();

      mockMvc.perform(post(baseUrl, postId)
              .contentType(MediaType.APPLICATION_JSON)
              .header("accessToken", accessToken)
              .content(objectMapper.writeValueAsString(request))
          )
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("요청에서 댓글 익명 여부가 null일 경우")
    public void testIsAnonymousNull() throws Exception {
      CommentCreateRequest request = CommentCreateRequest
          .builder()
          .postId(1L)
          .parentId(null)
          .isCommentAnonymous(null)
          .content("정상")
          .build();

      mockMvc.perform(post(baseUrl, postId)
              .contentType(MediaType.APPLICATION_JSON)
              .header("accessToken", accessToken)
              .content(objectMapper.writeValueAsString(request))
          )
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 내용이 null일 경우")
    public void testContentNull() throws Exception {
      CommentCreateRequest request = CommentCreateRequest
          .builder()
          .postId(1L)
          .parentId(null)
          .isCommentAnonymous(true)
          .content(null)
          .build();

      mockMvc.perform(post(baseUrl, postId)
              .contentType(MediaType.APPLICATION_JSON)
              .header("accessToken", accessToken)
              .content(objectMapper.writeValueAsString(request))
          )
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 내용이 공백일경우")
    public void testContentEmpty() throws Exception {
      CommentCreateRequest request = CommentCreateRequest
          .builder()
          .postId(1L)
          .parentId(null)
          .isCommentAnonymous(true)
          .content("")
          .build();

      mockMvc.perform(post(baseUrl, postId)
              .contentType(MediaType.APPLICATION_JSON)
              .header("accessToken", accessToken)
              .content(objectMapper.writeValueAsString(request))
          )
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 내용이 공백여러개일경우")
    public void testContentBlank() throws Exception {
      CommentCreateRequest request = CommentCreateRequest
          .builder()
          .postId(1L)
          .parentId(null)
          .isCommentAnonymous(true)
          .content("   ")
          .build();

      mockMvc.perform(post(baseUrl, postId)
              .contentType(MediaType.APPLICATION_JSON)
              .header("accessToken", accessToken)
              .content(objectMapper.writeValueAsString(request))
          )
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상적으로 댓글을 생성하는지")
    public void testCorrectRequest() throws Exception {
      CommentCreateRequest request = CommentCreateRequest
          .builder()
          .postId(1L)
          .parentId(null)
          .isCommentAnonymous(true)
          .content("정상")
          .build();

      mockMvc.perform(post(baseUrl, postId)
              .contentType(MediaType.APPLICATION_JSON)
              .header("accessToken", accessToken)
              .content(objectMapper.writeValueAsString(request))
          )
          .andExpect(status().isCreated());
    }

  }

  @Nested
  @DisplayName("댓글 삭제 API 실행 시")
  class Describe_deleteCommentApi {

    Long userId = 1L;

    Long postId = 1L;

    Long commentId = 1L;

    String deleteUrl = baseUrl + "/{commentId}";

    @Test
    @Disabled
    @DisplayName("없는 댓글 아이디를 삭제 할 경우")
    public void deleteIncorrectId() throws Exception {

      doThrow(new NotFoundException(ErrorCode.COMMENT_NOT_FOUND)).when(commentService)
          .deleteComment(userId, commentId);

      mockMvc.perform(delete(deleteUrl, postId, commentId)
              .contentType(MediaType.APPLICATION_JSON)
              .header("accessToken", accessToken))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("delete 요청인데 update로 요청할 경우")
    public void incorrectMethod() throws Exception {
      mockMvc.perform(post(deleteUrl, postId, commentId)
              .contentType(MediaType.APPLICATION_JSON)
              .header("accessToken", accessToken))
          .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("정상적인 댓글을 삭제할 경우")
    public void deleteCorrectId() throws Exception {

      doNothing().when(commentService).deleteComment(userId, commentId);

      mockMvc.perform(delete(deleteUrl, postId, commentId)
              .contentType(MediaType.APPLICATION_JSON)
              .header("accessToken", accessToken))
          .andExpect(status().isOk());
    }

  }

  @Nested
  @DisplayName("댓글 조회 API 실행시")
  class Describe_searchCommentsApi {

    Long postId = 1L;
    PageRequest page = PageRequest.of(1, 20);

    @Test
    @DisplayName("없는 post라면 400번 에러코드를 반환한다.")
    public void testPostNotFound() throws Exception {
      given(commentService.searchCommentsByPost(any(), any()))
          .willThrow(new NotFoundException(ErrorCode.POST_NOT_FOUND));

      mockMvc.perform(get(baseUrl, postId)
          .contentType(MediaType.APPLICATION_JSON)
          .header("accessToken", accessToken)
          .content(objectMapper.writeValueAsString(page))
      ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상 요청이라면")
    public void testCorrectRequest() throws Exception {
      mockMvc.perform(get(baseUrl, postId)
          .contentType(MediaType.APPLICATION_JSON)
          .header("accessToken", accessToken)
          .content(objectMapper.writeValueAsString(page))
      ).andExpect(status().isOk());
    }
  }
}