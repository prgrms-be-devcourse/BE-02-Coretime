package com.prgrms.coretime.comment.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.coretime.comment.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CommentController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CommentControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  CommentService commentService;

  String baseUrl = "/api/v1/comments/";

  @Nested
  @DisplayName("댓글 삭제 API 실행 시")
  class Describe_deleteCommentApi {

    Long id = 1L;

    @Test
    @DisplayName("없는 댓글 아이디를 삭제 할 경우")
    public void deleteIncorrectId() throws Exception {

      when(commentService.deleteComment(id)).thenThrow(IllegalArgumentException.class);

      mockMvc.perform(delete(baseUrl + "{commentId}", id)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("delete 요청인데 update로 요청할 경우")
    public void incorrectMethod() throws Exception {
      mockMvc.perform(post(baseUrl + "{commentId}", id)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("정상적인 댓글을 삭제할 경우")
    public void deleteCorrectId() throws Exception {

      doNothing().when(commentService).deleteComment(id);

      mockMvc.perform(delete(baseUrl + id)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
    }

  }

}