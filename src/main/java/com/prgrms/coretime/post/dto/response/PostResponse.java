package com.prgrms.coretime.post.dto.response;

import lombok.Builder;

import java.util.List;

public record PostResponse(Long postId, BoardSimpleResponse board,
                           UserSimpleResponse user, String title,
                           String content, Boolean isAnonymous,
                           List<CommentResponse> comments) {
    @Builder
    public PostResponse {
    }
}
