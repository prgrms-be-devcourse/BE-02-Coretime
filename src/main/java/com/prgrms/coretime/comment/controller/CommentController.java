package com.prgrms.coretime.comment.controller;

import com.prgrms.coretime.comment.dto.request.CommentCreateRequest;
import com.prgrms.coretime.comment.dto.response.CommentCreateResponse;
import com.prgrms.coretime.comment.dto.response.CommentsOnPostResponse;
import com.prgrms.coretime.comment.service.CommentService;
import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.common.jwt.JwtPrincipal;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  /**
   * TODO : 추후 현재 로그인 중인 User 포함해서 API 수정
   */
  @PostMapping
  public ResponseEntity<ApiResponse<CommentCreateResponse>> createComment(
      @AuthenticationPrincipal JwtPrincipal principal,
      @PathVariable Long postId,
      @Validated
      @RequestBody CommentCreateRequest commentCreateRequest) throws URISyntaxException {

    CommentCreateResponse response = commentService.createComment(principal.userId,
        commentCreateRequest);
    URI location = new URI("/api/v1/posts/" + commentCreateRequest.getPostId());
    return ResponseEntity.created(location).body(new ApiResponse("댓글 생성 성공", response));
  }

  @DeleteMapping("/{commentId}")
  public ResponseEntity<ApiResponse<Void>> deleteComment(
      @AuthenticationPrincipal JwtPrincipal principal,
      @PathVariable Long commentId) {
    commentService.deleteComment(principal.userId, commentId);
    return ResponseEntity.ok(new ApiResponse("댓글 삭제 성공"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<Page<CommentsOnPostResponse>>> searchComments(
      @PathVariable Long postId, Pageable pageable) {
    return ResponseEntity.ok(
        new ApiResponse<>("댓글 조회 성공", commentService.searchCommentsByPost(postId, pageable)));
  }

}
