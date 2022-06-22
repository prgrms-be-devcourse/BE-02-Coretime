package com.prgrms.coretime.post.controller;

import com.prgrms.coretime.post.dto.request.PostCreateRequest;
import com.prgrms.coretime.post.dto.response.PostIdResponse;
import com.prgrms.coretime.post.dto.response.PostResponse;
import com.prgrms.coretime.post.dto.response.PostSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/posts")
@RestController
public class PostController {
    @GetMapping("/hot")
    public Page<PostSimpleResponse> showHotPosts(
            @RequestParam @PageableDefault Pageable pageable
    ) {
    }

    @GetMapping("/best")
    public Page<PostSimpleResponse> showBestPosts(
            @RequestParam @PageableDefault Pageable pageable
    ) {
    }

    @GetMapping("/my")
    public Page<PostSimpleResponse> showMyPosts(
            @RequestParam @PageableDefault Pageable pageable
    ) {
    }

    @GetMapping("/mycomment")
    public Page<PostSimpleResponse> showMyCommentedPosts(
            @RequestParam @PageableDefault Pageable pageable
    ) {
    }

    @GetMapping("/{postId}")
    public PostResponse showPost(
            @PathVariable(name = "postId") Long postId
    ) {
    }

    @PatchMapping("/{postId}")
    public PostIdResponse updatePost(
            @PathVariable(name = "postId") Long postId,
            @RequestBody PostCreateRequest request
    ) {
    }

    @DeleteMapping("/{postId}")
    public void deletePost(
            @PathVariable(name = "postId") Long postId
    ) {
    }

    @GetMapping()
    public Page<PostSimpleResponse> searchPosts(
            @RequestParam String keyword,
            @RequestParam @PageableDefault Pageable pageable
    ) {
    }

    @PostMapping("/{postId}/like")
    public void likePost(
            @PathVariable(name = "postId") Long postId
    ) {
    }

    @DeleteMapping("/{postId}/like")
    public void unlikePost(
            @PathVariable(name = "postId") Long postId
    ) {
    }
}
