package com.prgrms.coretime.comment.service;

import com.prgrms.coretime.comment.domain.repository.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentLikeService {

  private final CommentLikeRepository commentLikeRepository;

  public void createLike(Long id) {

  }

}
