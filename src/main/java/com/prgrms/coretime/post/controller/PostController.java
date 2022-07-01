package com.prgrms.coretime.post.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.common.jwt.JwtPrincipal;
import com.prgrms.coretime.post.dto.request.PostUpdateRequest;
import com.prgrms.coretime.post.dto.response.PostIdResponse;
import com.prgrms.coretime.post.dto.response.PostResponse;
import com.prgrms.coretime.post.dto.response.PostSimpleResponse;
import com.prgrms.coretime.post.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  @GetMapping("/hot")
  public ResponseEntity<ApiResponse<Page<PostSimpleResponse>>> showHotPosts(
      @RequestParam(required = false) @PageableDefault(
          sort = {"createdAt"},
          direction = Sort.Direction.DESC
      ) Pageable pageable
  ) {
    return ResponseEntity.ok(
        new ApiResponse<>(
            "Hot 게시글 목록",
            postService.getHotPosts(pageable)
        )
    );
  }

  @GetMapping("/best")
  public ResponseEntity<ApiResponse<Page<PostSimpleResponse>>> showBestPosts(
      @RequestParam(required = false) @PageableDefault(
          sort = {"createdAt"},
          direction = Sort.Direction.DESC
      ) Pageable pageable
  ) {
    return ResponseEntity.ok(
        new ApiResponse<>(
            "Best 게시글 목록",
            postService.getBestPosts(pageable)
        )
    );
  }

  @GetMapping("/my")
  public ResponseEntity<ApiResponse<Page<PostSimpleResponse>>> showMyPosts(
      @RequestParam(required = false) @PageableDefault(
          sort = {"createdAt"},
          direction = Sort.Direction.DESC
      ) Pageable pageable,
      @AuthenticationPrincipal JwtPrincipal principal
  ) {
    return ResponseEntity.ok(
        new ApiResponse<>(
            "내 게시글 목록",
            postService.getPostsByUser(principal.userId, pageable)
        )
    );
  }

  @GetMapping("/mycomment")
  public ResponseEntity<ApiResponse<Page<PostSimpleResponse>>> showMyCommentedPosts(
      @RequestParam(required = false) @PageableDefault(
          sort = {"createdAt"},
          direction = Sort.Direction.DESC
      ) Pageable pageable,
      @AuthenticationPrincipal JwtPrincipal principal
  ) {
    return ResponseEntity.ok(
        new ApiResponse<>(
            "내 게시글 목록",
            postService.getPostsThatUserCommentedAt(principal.userId, pageable)
        )
    );
  }

  @GetMapping("/{postId}")
  public ResponseEntity<ApiResponse<PostResponse>> showPost(
      @PathVariable(name = "postId") Long postId
  ) {
    return ResponseEntity.ok(
        new ApiResponse<>(
            "게시글 상세",
            postService.getPost(postId)
        )
    );
  }

  @PatchMapping("/{postId}")
  public ResponseEntity<ApiResponse<PostIdResponse>> updatePost(
      @PathVariable(name = "postId") Long postId,
      @RequestBody @Validated PostUpdateRequest request
  ) {
    return ResponseEntity.ok(
        new ApiResponse<>(
            "게시글 수정",
            postService.updatePost(postId, request)
        )
    );
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<ApiResponse<Void>> deletePost(
      @PathVariable(name = "postId") Long postId
  ) {
    postService.deletePost(postId);
    return ResponseEntity.ok(
        new ApiResponse<>(
            "게시글 삭제"
        )
    );
  }

  @GetMapping()
  public ResponseEntity<ApiResponse<Page<PostSimpleResponse>>> searchPosts(
      @RequestParam String keyword,
      @RequestParam(required = false) @PageableDefault(
          sort = {"createdAt"},
          direction = Sort.Direction.DESC
      ) Pageable pageable
  ) {
    return ResponseEntity.ok(
        new ApiResponse<>(
            "전체 게시글 검색",
            postService.searchPosts(keyword, pageable)
        )
    );
  }

  @PostMapping("/{postId}/like")
  public ResponseEntity<ApiResponse<Void>> likePost(
      @PathVariable(name = "postId") Long postId,
      @AuthenticationPrincipal JwtPrincipal principal
  ) {
    postService.likePost(principal.userId, postId);
    return ResponseEntity.ok(
        new ApiResponse<>(
            "게시글 좋아요"
        )
    );
  }

  @DeleteMapping("/{postId}/like")
  public ResponseEntity<ApiResponse<Void>> unlikePost(
      @PathVariable(name = "postId") Long postId,
      @AuthenticationPrincipal JwtPrincipal principal
  ) {
    postService.unlikePost(principal.userId, postId);
    return ResponseEntity.ok(
        new ApiResponse<>(
            "게시글 좋아요 취소"
        )
    );
  }
}
