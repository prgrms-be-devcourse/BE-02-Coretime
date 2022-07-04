package com.prgrms.coretime.comment.service;

import com.mysema.commons.lang.Assert;
import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.comment.domain.repository.CommentRepository;
import com.prgrms.coretime.comment.dto.request.CommentCreateRequest;
import com.prgrms.coretime.comment.dto.response.CommentCreateResponse;
import com.prgrms.coretime.common.ErrorCode;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.repository.PostRepository;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  // 1. 생성할때 기존 유저가 익명 댓글을 썼는지 확인.
  // 2. 익명댓글을 썼다면 기존 유저가 사용한 익명 번호를 가져와야함.
  // 3. 처음 익명댓글을 쓰는 거라면 post에서 가져오기.
  public CommentCreateResponse createComment(Long userId,
      CommentCreateRequest commentCreateRequest) {
    User currentUser = getCurrentUser(userId);
    Post post = getPost(commentCreateRequest.getPostId());
    Comment parent = commentCreateRequest.getParentId() == null ?
        null : getComment(commentCreateRequest.getParentId());

    // 기존에 유저가 댓글을 썼다면 익명번호 가져오기
    Integer seq = commentCreateRequest.getIsCommentAnonymous() ? Integer.valueOf(
        commentRepository.findFirstByUserIdAndPostId(userId, post.getId())
            .map(comment -> comment.getAnonymousSeq())
            .orElse(post.getAnonymousSeqAndAdd())) : null;

    Comment comment = Comment.builder()
        .user(currentUser)
        .post(post)
        .parent(parent)
        .isAnonymous(commentCreateRequest.getIsCommentAnonymous())
        .anonymousSeq(seq)
        .content(commentCreateRequest.getContent())
        .build();

    commentRepository.save(comment);

    return CommentCreateResponse.of(currentUser, post, comment);
  }

  public void deleteComment(Long userId, Long commentId) {
    Comment comment = getComment(commentId);
    checkValidUser(userId, commentId);
    comment.updateDelete();
  }

  private Post getPost(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));
  }

  private Comment getComment(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
  }

  private User getCurrentUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
  }

  private void checkValidUser(Long currentUserId, Long targetUserId) {
    Assert.isFalse(currentUserId == targetUserId, ErrorCode.BAD_REQUEST.getMessage());
  }

}
