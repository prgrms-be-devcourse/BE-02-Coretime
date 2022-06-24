package com.prgrms.coretime.post.controller;

import com.prgrms.coretime.post.dto.request.PostUpdateRequest;
import com.prgrms.coretime.post.dto.response.PostIdResponse;
import com.prgrms.coretime.post.dto.response.PostResponse;
import com.prgrms.coretime.post.dto.response.PostSimpleResponse;
import com.prgrms.coretime.post.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

//    @GetMapping("/hot")
//    public Page<PostSimpleResponse> showHotPosts(
//            @RequestParam(required = false) @PageableDefault(
//                    sort = {"created_at"},
//                    direction = Sort.Direction.DESC
//            ) Pageable pageable
//    ) {
//    }
//
//    @GetMapping("/best")
//    public Page<PostSimpleResponse> showBestPosts(
//            @RequestParam(required = false) @PageableDefault(
//                    sort = {"created_at"},
//                    direction = Sort.Direction.DESC
//            ) Pageable pageable
//    ) {
//    }

  @GetMapping("/my")
  public Page<PostSimpleResponse> showMyPosts(
      @RequestParam(required = false) @PageableDefault(
          sort = {"created_at"},
          direction = Sort.Direction.DESC
      ) Pageable pageable,
      Long userId
  ) {
    return postService.getPostsByUser(userId, pageable);
  }

//    @GetMapping("/mycomment")
//    public Page<PostSimpleResponse> showMyCommentedPosts(
//            @RequestParam(required = false) @PageableDefault(
//                    sort = {"created_at"},
//                    direction = Sort.Direction.DESC
//            ) Pageable pageable
//    ) {
//    }

  @GetMapping("/{postId}")
  public PostResponse showPost(
      @PathVariable(name = "postId") Long postId
  ) {
    return postService.getPost(postId);
  }

  @PatchMapping("/{postId}")
  public PostIdResponse updatePost(
      @PathVariable(name = "postId") Long postId,
      @RequestBody @Validated PostUpdateRequest request
  ) {
    return postService.updatePost(postId, request);
  }

  @DeleteMapping("/{postId}")
  public void deletePost(
      @PathVariable(name = "postId") Long postId
  ) {
    postService.deletePost(postId);
  }

  @GetMapping()
  public Page<PostSimpleResponse> searchPosts(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) @PageableDefault(
          sort = {"created_at"},
          direction = Sort.Direction.DESC
      ) Pageable pageable
  ) {
    return postService.searchPosts(keyword, pageable);
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
