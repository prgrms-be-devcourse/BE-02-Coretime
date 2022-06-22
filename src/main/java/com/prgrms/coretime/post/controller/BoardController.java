package com.prgrms.coretime.post.controller;

import com.prgrms.coretime.post.dto.request.PostCreateRequest;
import com.prgrms.coretime.post.dto.response.PostIdResponse;
import com.prgrms.coretime.post.dto.response.PostSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/boards")
@RestController
public class BoardController {
    @GetMapping("/{boardId}/posts")
    public Page<PostSimpleResponse> showPostsByBoard(
            @PathVariable(name = "boardId") Long boardId,
            @RequestParam @PageableDefault Pageable pageable
    ) {
    }

    @PostMapping("/{boardId}/posts")
    public PostIdResponse createPost(
            @PathVariable(name = "boardId") Long boardId,
            @RequestBody PostCreateRequest request
    ) {
    }

    @GetMapping("/{boardId}/posts")
    public Page<PostSimpleResponse> searchPostsByBoard(
            @PathVariable(name = "boardId") Long boardId,
            @RequestParam String keyword,
            @RequestParam @PageableDefault Pageable pageable
    ) {
    }
}
