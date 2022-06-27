package com.prgrms.coretime.post.controller;

import com.prgrms.coretime.common.ApiResponse;
import com.prgrms.coretime.post.dto.request.PostCreateRequest;
import com.prgrms.coretime.post.dto.response.PostIdResponse;
import com.prgrms.coretime.post.dto.response.PostSimpleResponse;
import com.prgrms.coretime.post.service.PostService;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/boards")
@RestController
public class BoardController {
    private final PostService postService;

    public BoardController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{boardId}/posts")
    public ResponseEntity<ApiResponse<Page<PostSimpleResponse>>> showPostsByBoard(
            @PathVariable(name = "boardId") Long boardId,
            @RequestParam(required = false) @PageableDefault(
                    sort = {"created_at"},
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(
            new ApiResponse<>(
                "게시판별 게시글 목록",
                postService.getPostsByBoard(boardId, pageable)
            )
        );
    }

    @GetMapping("/{boardId}/posts/search")
    public ResponseEntity<ApiResponse<Page<PostSimpleResponse>>> searchPostsAtBoard(
        @PathVariable(name = "boardId") Long boardId,
        @RequestParam String keyword,
        @RequestParam(required = false) @PageableDefault(
            sort = {"created_at"},
            direction = Sort.Direction.DESC
        ) Pageable pageable
    ) {
        return ResponseEntity.ok(
            new ApiResponse<>(
                "게시판별 게시글 목록/검색",
                postService.searchPostsAtBoard(boardId, keyword, pageable)
            )
        );
    }

    @PostMapping("/{boardId}/posts")
    public ResponseEntity<ApiResponse<PostIdResponse>> createPost(
            @PathVariable(name = "boardId") Long boardId,
            Long userId,
            @RequestBody @Validated PostCreateRequest request
    ) {
        return ResponseEntity.created(URI.create("")).body(
            new ApiResponse<>(
                "게시글 생성",
                postService.createPost(boardId, userId, request)
            )
        );
    }
}
