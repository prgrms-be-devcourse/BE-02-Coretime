package com.prgrms.coretime.comment.controller;

import com.prgrms.coretime.comment.dto.request.CommentCreateRequest;
import com.prgrms.coretime.comment.service.CommentService;
import com.prgrms.coretime.common.ApiResponse;
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

  @PostMapping
  public ResponseEntity<ApiResponse> createComment(
      @RequestBody CommentCreateRequest commentCreateRequest) {

    return null;
  }

}
