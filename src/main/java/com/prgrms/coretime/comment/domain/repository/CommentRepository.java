package com.prgrms.coretime.comment.domain.repository;

import com.prgrms.coretime.comment.domain.Comment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

  @Query("select c from Comment c where c.user.id = :userId and c.post.id = :postId and c.isAnonymous = true")
  Optional<Comment> findFirstByUserIdAndPostId(Long userId, Long postId);
}
