package com.prgrms.coretime.comment.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.coretime.comment.service.CommentLikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CommentLikeController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CommentLikeControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  CommentLikeService commentLikeService;

  String baseUrl = "/api/v1/comments/";

  @Nested
  @DisplayName("댓글 좋아요 생성(POST) API 실행 시")
  class Describe_createCommentLike {

    Long id = 1L;

    @Nested
    @DisplayName("Edge case 테스트 중에서 ")
    class Describe_EdgeCase {

      @Test
      @DisplayName("유효하지 않은 Comment id를 받으면 실패")
      public void testIncorrectId() throws Exception {

        when(commentLikeService.createLike(id)).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(post(baseUrl + "{commentId}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

      }

      @Test
      @DisplayName("HTTP POST 아니면 실패")
      public void testNotAllowedHttpMethod() throws Exception {
        mockMvc.perform(get(baseUrl + "{commentId}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(delete(baseUrl + "{commentId}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(put(baseUrl + "{commentId}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed());
      }

    }

    @Nested
    @DisplayName("Happy Path 테스트 중에서 ")
    class Describe_HappyPath {

      @Test
      @DisplayName("정상적인 요청을 받으면 통과")
      public void testCorrectHttpRequest() throws Exception {
        doNothing().when(commentLikeService).createLike(id);

        mockMvc.perform(post(baseUrl + "{commentId}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
      }

    }

  }

  @Nested
  @DisplayName("댓글 좋아요 삭제(Delete) API 실행 시")
  class Describe_deleteCommentLike {

    Long id;

    @Nested
    @DisplayName("Edge case 테스트 중에서 ")
    class Describe_EdgeCase {

      @Test
      @DisplayName("유효하지 않은 comment id를 받으면 실패")
      public void test() {

      }

      @Test
      @DisplayName("HTTP Delete 아니면 실패")
      public void testHttpMethodNotAllowed() {

      }

    }

    @Nested
    @DisplayName("Happy Path 테스트 중에서 ")
    class Describe_HappyPath {

      @Test
      @DisplayName("정상적인 요청을 받을 경우 삭제 수행!")
      public void testCorrectHttpRequest() {

      }

    }
  }

}