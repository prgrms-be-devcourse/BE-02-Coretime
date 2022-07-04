package com.prgrms.coretime.comment.domain.repository;

import com.prgrms.coretime.comment.dto.response.CommentOneResponse;
import com.prgrms.coretime.comment.dto.response.CommentsOnPostResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {

  Page<CommentsOnPostResponse> findByPost(Long postId, Pageable pageable);

  Optional<CommentOneResponse> findBestCommentByPost(Long postId);
}
