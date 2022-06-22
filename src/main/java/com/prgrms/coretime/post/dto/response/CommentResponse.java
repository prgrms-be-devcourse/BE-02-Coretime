package com.prgrms.coretime.post.dto.response;

import lombok.Builder;

public record CommentResponse(Long commentId, UserSimpleResponse user,
                              String content, Boolean isDelete, Boolean isAnonymous) {
    @Builder
    public CommentResponse {
    }
}
