package com.prgrms.coretime.post.controller;

import com.prgrms.coretime.common.ApiResponse;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
          sort = {"created_at"},
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
          sort = {"created_at"},
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
          sort = {"created_at"},
          direction = Sort.Direction.DESC
      ) Pageable pageable,
      Long userId
  ) {
    return ResponseEntity.ok(
        new ApiResponse<>(
            "내 게시글 목록",
            postService.getPostsByUser(userId, pageable)
        )
    );
  }

  @GetMapping("/mycomment")
  public ResponseEntity<ApiResponse<Page<PostSimpleResponse>>> showMyCommentedPosts(
      @RequestParam(required = false) @PageableDefault(
          sort = {"created_at"},
          direction = Sort.Direction.DESC
      ) Pageable pageable,
      Long userId
  ) {
    return ResponseEntity.ok(
        new ApiResponse<>(
            "내 게시글 목록",
            postService.getPostsThatUserCommentedAt(userId, pageable)
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
  public void deletePost(
      @PathVariable(name = "postId") Long postId
  ) {
    postService.deletePost(postId);
  }

  @GetMapping()
  public ResponseEntity<ApiResponse<Page<PostSimpleResponse>>> searchPosts(
      @RequestParam String keyword,
      @RequestParam(required = false) @PageableDefault(
          sort = {"created_at"},
          direction = Sort.Direction.DESC
      ) Pageable pageable
  ) {
    return ResponseEntity.ok(
        new ApiResponse<>(
            "게시글 검색",
            postService.searchPosts(keyword, pageable)
        )
    );
  }

  @PostMapping("/{postId}/like")
  public void likePost(
      @PathVariable(name = "postId") Long postId,
      Long userId
  ) {
    postService.likePost(userId, postId);
  }

  @DeleteMapping("/{postId}/like")
  public void unlikePost(
      @PathVariable(name = "postId") Long postId,
      Long userId
  ) {
    postService.unlikePost(userId, postId);
  }
}
