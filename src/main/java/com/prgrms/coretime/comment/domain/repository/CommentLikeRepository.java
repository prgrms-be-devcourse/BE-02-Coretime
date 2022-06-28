package com.prgrms.coretime.comment.domain.repository;

import com.prgrms.coretime.comment.domain.CommentLike;
import com.prgrms.coretime.comment.domain.CommentLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {

}
