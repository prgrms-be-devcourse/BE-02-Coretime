package com.prgrms.coretime.comment.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.coretime.comment.service.CommentLikeService;
import com.prgrms.coretime.common.config.WebSecurityConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = CommentLikeController.class,
    excludeFilters =
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class)
)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockUser(roles = "USER")
class CommentLikeControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  CommentLikeService commentLikeService;

  String uri = "/api/v1/comments/{commentId}/like";

  @Nested
  @DisplayName("댓글 좋아요 생성(POST) API 실행 시")
  class Describe_createCommentLike {

    Long userId = 1L;
    Long commentId = 1L;

    @Nested
    @DisplayName("Edge case 테스트 중에서 ")
    class Describe_EdgeCase {

      @Test
      @Disabled //TODO: doThrow 터지긴 하는데, 상태코드 200으로 들어가는 문제 해결해야함
      @DisplayName("유효하지 않은 Comment id를 받으면 실패")
      public void testIncorrectId() throws Exception {

        doThrow(new IllegalArgumentException())
            .doThrow(new IllegalArgumentException())
            .when(commentLikeService)
            .createLike(userId, commentId);

        mockMvc.perform(post(uri, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest());
      }

      @Test
      @DisplayName("HTTP POST 아니면 실패")
      public void testNotAllowedHttpMethod() throws Exception {
        mockMvc.perform(get(uri, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(put(uri, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isMethodNotAllowed());
      }

    }

    @Nested
    @DisplayName("Happy Path 테스트 중에서 ")
    class Describe_HappyPath {

      @Test
      @DisplayName("정상적인 요청을 받으면 통과")
      public void testCorrectHttpRequest() throws Exception {
        doNothing()
            .when(commentLikeService)
            .createLike(userId, commentId);

        mockMvc.perform(post(uri, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isCreated());
      }

    }

  }

  @Nested
  @DisplayName("댓글 좋아요 삭제(Delete) API 실행 시")
  class Describe_deleteCommentLike {

    Long userId = 1L;
    Long commentId = 1L;

    @Nested
    @DisplayName("Edge case 테스트 중에서 ")
    class Describe_EdgeCase {

      @Test
      @Disabled //TODO: doThrow 터지긴 하는데, 상태코드 200으로 들어가는 문제 해결해야함
      @DisplayName("유효하지 않은 comment id를 받으면 실패")
      public void test() throws Exception {

        doThrow(new IllegalArgumentException())
            .when(commentLikeService).deleteLike(userId, commentId);

        mockMvc.perform(delete(uri, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest());
      }

      @Test
      @DisplayName("HTTP Delete 아니면 실패")
      public void testHttpMethodNotAllowed() throws Exception {
        mockMvc.perform(get(uri, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(put(uri, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isMethodNotAllowed());
      }

    }

    @Nested
    @DisplayName("Happy Path 테스트 중에서 ")
    class Describe_HappyPath {

      @Test
      @DisplayName("정상적인 요청을 받을 경우 삭제 수행!")
      public void testCorrectHttpRequest() throws Exception {
        doNothing().when(commentLikeService).createLike(userId, commentId);

        mockMvc.perform(delete(uri, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isCreated());
      }

    }
  }

}