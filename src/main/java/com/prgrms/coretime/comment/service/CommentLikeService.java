package com.prgrms.coretime.comment.service;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.comment.domain.CommentLike;
import com.prgrms.coretime.comment.domain.CommentLikeId;
import com.prgrms.coretime.comment.domain.repository.CommentLikeRepository;
import com.prgrms.coretime.comment.domain.repository.CommentRepository;
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

  private final CommentRepository commentRepository;
  private final CommentLikeRepository commentLikeRepository;

  public void createLike(Long userId, Long commentId) {
    CommentLikeId commentLikeId = new CommentLikeId(userId, commentId);
    if (isPresentLike(commentLikeId)) {
      throw new IllegalArgumentException("해당 좋아요가 이미 존재합니다.");
    }
    Comment comment = getComment(commentId);

    /*
     * TODO : 저장 로직 안넣음
     *  추후 통합테스트 시 user 넣기
     * */
  }

  // 좋아요 삭제는 무조건 현재 유저만이 해당 좋아요를 삭제할 수 있다.
  public void deleteLike(Long userId, Long commentId) {
    CommentLikeId commentLikeId = new CommentLikeId(userId, commentId);
    if (!isPresentLike(commentLikeId)) {
      throw new IllegalArgumentException("해당 좋아요가 존재하지 않습니다.");
    }
    commentLikeRepository.deleteById(commentLikeId);
  }

  private Comment getComment(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new IllegalArgumentException("현재 댓글이 존재하지 않습니다."));
  }

  private CommentLike getCommentLike(Long userId, Long commentId) {
    return commentLikeRepository.findById(new CommentLikeId(userId, commentId))
        .orElseThrow(() -> new IllegalArgumentException("해당 좋아요가 존재하지 않습니다."));
  }

  private boolean isPresentLike(CommentLikeId commentLikeId) {
    /**
     * TODO : 일단 exists 쓰고, 추후 limit로 리팩터 하자 (참고 : jpql은 limit 지원 안해)
     * */
    return commentLikeRepository.existsById(commentLikeId);
  }

}

