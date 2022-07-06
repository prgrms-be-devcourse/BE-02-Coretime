package com.prgrms.coretime.comment.service;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.comment.domain.CommentLike;
import com.prgrms.coretime.comment.domain.CommentLikeId;
import com.prgrms.coretime.comment.domain.repository.CommentLikeRepository;
import com.prgrms.coretime.comment.domain.repository.CommentRepository;
import com.prgrms.coretime.common.ErrorCode;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TODO : 비동기 처리 고려
 */

@Service
@Transactional
@RequiredArgsConstructor
public class CommentLikeService {

  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final CommentLikeRepository commentLikeRepository;

  public void createLike(Long userId, Long commentId) {
    CommentLikeId commentLikeId = new CommentLikeId(userId, commentId);
    if (isPresentLike(commentLikeId)) {
      throw new IllegalArgumentException(ErrorCode.COMMENT_LIKE_ALREADY_EXISTS.getMessage());
    }
    User currentUser = getCurrentUser(userId);
    Comment comment = getComment(commentId);

    commentLikeRepository.save(new CommentLike(currentUser, comment));
  }

  public void deleteLike(Long userId, Long commentId) {
    CommentLikeId commentLikeId = new CommentLikeId(userId, commentId);
    if (!isPresentLike(commentLikeId)) {
      throw new IllegalArgumentException(ErrorCode.COMMENT_LIKE_NOT_FOUND.getMessage());
    }
    commentLikeRepository.deleteById(commentLikeId);
  }

  private Comment getComment(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
  }

  private CommentLike getCommentLike(Long userId, Long commentId) {
    return commentLikeRepository.findById(new CommentLikeId(userId, commentId))
        .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
  }

  private boolean isPresentLike(CommentLikeId commentLikeId) {
    return commentLikeRepository.existsById(commentLikeId);
  }

  private User getCurrentUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
  }

}

