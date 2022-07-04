package com.prgrms.coretime.comment.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.coretime.comment.service.CommentService;
import com.prgrms.coretime.common.config.WebSecurityConfig;
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

@WebMvcTest(controllers = CommentController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class))
@MockBean(JpaMetamodelMappingContext.class)
@WithMockUser("USERS")
class CommentControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  CommentService commentService;

  String baseUrl = "/api/v1/comments";

  /*
   * TODO
   * */
  @Nested
  @DisplayName("댓글 생성 API 실행시")
  class Describe_createCommentApi {

  }

  @Nested
  @DisplayName("댓글 삭제 API 실행 시")
  class Describe_deleteCommentApi {

    Long userId = 1L;

    Long commentId = 1L;

    String deleteUrl = baseUrl + "/{commentId}";

    @Test
    @DisplayName("없는 댓글 아이디를 삭제 할 경우")
    public void deleteIncorrectId() throws Exception {

      doThrow(new IllegalArgumentException()).when(commentService).deleteComment(userId, commentId);

      mockMvc.perform(
              delete(deleteUrl, commentId).contentType(MediaType.APPLICATION_JSON).with(csrf()))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("delete 요청인데 update로 요청할 경우")
    public void incorrectMethod() throws Exception {
      mockMvc.perform(
              post(deleteUrl, commentId).contentType(MediaType.APPLICATION_JSON).with(csrf()))
          .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("정상적인 댓글을 삭제할 경우")
    public void deleteCorrectId() throws Exception {

      doNothing().when(commentService).deleteComment(userId, commentId);

      mockMvc.perform(
              delete(deleteUrl, commentId).contentType(MediaType.APPLICATION_JSON).with(csrf()))
          .andExpect(status().isOk());
    }

  }

}