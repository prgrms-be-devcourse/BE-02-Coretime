package com.prgrms.coretime.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.coretime.comment.service.CommentLikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
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
  @DisplayName("댓글 좋아요 API 실행 시")
  class Describe_createCommentLike {

    @Nested
    @DisplayName("Edge case 테스트 중에서 ")
    class Describe_EdgeCase {

      @Test
      @DisplayName("유효하지 않은 Comment id를 받으면 실패")
      public void testIncorrectId() {

      }

      @Test
      @DisplayName("HTTP POST 아니면 실패")
      public void testNotAllowedHttpMethod() {

      }

    }

    @Nested
    @DisplayName("Happy Path 테스트 중에서 ")
    class Describe_HappyPath {

      @Test
      @DisplayName("정상적인 요청을 받으면 통과")
      public void testCorrectHttpRequest() {

      }

    }

  }
  
}