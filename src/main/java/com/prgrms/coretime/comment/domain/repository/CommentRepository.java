package com.prgrms.coretime.comment.domain.repository;

import com.prgrms.coretime.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
