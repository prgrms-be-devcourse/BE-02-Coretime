package com.prgrms.coretime.comment.service;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.comment.domain.repository.CommentRepository;
import com.prgrms.coretime.comment.dto.request.CommentCreateRequest;
import com.prgrms.coretime.comment.dto.response.CommentCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;

  public CommentCreateResponse createComment(CommentCreateRequest commentCreateRequest) {

    return null;
  }


  private Comment getParentComment(Long commentId) {
    /**
     * TODO: Exception 종류 바꾸기
     * */
    return commentRepository.findById(commentId)
        .orElseThrow(IllegalArgumentException::new);
  }
}
