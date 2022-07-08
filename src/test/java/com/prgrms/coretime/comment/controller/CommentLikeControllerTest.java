package com.prgrms.coretime.comment.controller;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.coretime.AcceptanceTest;
import com.prgrms.coretime.comment.service.CommentLikeService;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("댓글 생성 및 조회 통합/인수 테스트")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class CommentLikeControllerTest extends AcceptanceTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  CommentLikeService commentLikeService;

  @Autowired
  private SchoolRepository schoolRepository;

  private String baseUrl = "/api/v1/comments/{commentId}/like";

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

  @AfterAll
  void teardown() {
    userRepository.deleteAllInBatch();
    schoolRepository.deleteAll();
  }

  @Nested
  @DisplayName("댓글 좋아요 생성(POST) API 실행 시")
  class Describe_createCommentLike {

    Long userId = 1L;
    Long commentId = 1L;

    @Test
    @DisplayName("정상적인 요청을 받으면 통과")
    public void testCorrectHttpRequest() throws Exception {
      doNothing()
          .when(commentLikeService)
          .createLike(userId, commentId);

      mockMvc.perform(post(baseUrl, commentId)
          .contentType(MediaType.APPLICATION_JSON)
          .header("accessToken", accessToken)
      ).andExpect(status().isCreated());
    }

  }

  @Nested
  @DisplayName("댓글 좋아요 삭제(Delete) API 실행 시")
  class Describe_deleteCommentLike {

    Long userId = 1L;
    Long commentId = 1L;

    @Test
    @DisplayName("정상적인 요청을 받을 경우 삭제 수행!")
    public void testCorrectHttpRequest() throws Exception {
      doNothing().when(commentLikeService).createLike(userId, commentId);

      mockMvc.perform(delete(baseUrl, commentId)
          .contentType(MediaType.APPLICATION_JSON)
          .header("accessToken", accessToken)
      ).andExpect(status().isOk());
    }

  }
}
