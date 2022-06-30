package com.prgrms.coretime.post.controller;

import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardType;
import com.prgrms.coretime.post.domain.repository.BoardRepository;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private SchoolRepository schoolRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BoardRepository boardRepository;

  User currentUser;
  Board board;

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
        .nickname("테스트1")
        .email("test1@test1")
        .name("테식이1")
        .school(school)
        .password("1q2w3e")
        .build());
  }

  @Test
  @DisplayName("게시판 별 게시글 조회 api 테스트")
  public void testShowPostsByBoard() throws Exception {
    mockMvc.perform(get("/api/v1/boards/{boardId}/posts", board.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("게시판 별 게시글 검색 api 테스트")
  public void testSearchPostsAtBoard() throws Exception {
    mockMvc.perform(get("/api/v1/boards/{boardId}/posts/search", board.getId())
            .param("keyword", "테스트")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("게시판 별 게시글 검색 api 키워드 없이 검색 테스트")
  public void testSearchPostsAtBoardWithNoKeyword() throws Exception {
    mockMvc.perform(get("/api/v1/boards/{boardId}/posts/search", board.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }



}