package com.prgrms.coretime.comment.controller;

import com.prgrms.coretime.comment.dto.request.CommentCreateRequest;
import com.prgrms.coretime.comment.dto.response.CommentCreateResponse;
import com.prgrms.coretime.comment.service.CommentService;
import com.prgrms.coretime.common.ApiResponse;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  /**
   * TODO : 추후 현재 로그인 중인 User 포함해서 API 수정
   */
  @PostMapping
  public ResponseEntity<ApiResponse<CommentCreateResponse>> createComment(
      // 현재 로그인한 User 필요
      @RequestBody CommentCreateRequest commentCreateRequest) throws URISyntaxException {

    CommentCreateResponse data = commentService.createComment(commentCreateRequest);
    URI location = new URI("/api/v1/posts{postId}");
    return ResponseEntity.created(location).body(new ApiResponse("댓글 생성 성공", data));
  }

}
