package com.prgrms.coretime.post.dto.response;

import lombok.Builder;

public record PostSimpleResponse(Long postId, BoardSimpleResponse board,
                                 UserSimpleResponse user, String title,
                                 String content, Boolean isAnonymous) {
    @Builder
    public PostSimpleResponse {
    }
}
