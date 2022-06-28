package com.prgrms.coretime.comment.service;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.comment.domain.repository.CommentLikeRepository;
import com.prgrms.coretime.comment.domain.repository.CommentRepository;
import com.prgrms.coretime.common.ErrorCode;
import com.prgrms.coretime.common.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentLikeService {

  private final CommentRepository commentRepository;
  private final CommentLikeRepository commentLikeRepository;

  public Void createLike(Long commentId) {
    /*
     * TODO user 추후 비즈니스 로직 작성, 아 나중에 다시 뜯어야하는데 아몰라!
     * 비동기 처리 고려
     * */
    Comment comment = getComment(commentId);

    return null;
  }

  public Void deleteLike(Long commentId) {

    Comment comment = getComment(commentId);

    return null;
  }


  private Comment getComment(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.INVALID_INPUT_VALUE));
  }

}
