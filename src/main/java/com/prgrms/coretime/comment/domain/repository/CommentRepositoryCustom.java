package com.prgrms.coretime.comment.domain.repository;

import com.prgrms.coretime.comment.dto.response.CommentsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {

  Page<CommentsResponse> findByPostId(Long postId, Pageable pageable);
}
