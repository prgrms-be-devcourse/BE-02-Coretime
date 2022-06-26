package com.prgrms.coretime.comment.service;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.comment.domain.repository.CommentRepository;
import com.prgrms.coretime.comment.dto.request.CommentCreateRequest;
import com.prgrms.coretime.comment.dto.response.CommentCreateResponse;
import com.prgrms.coretime.common.error.NotFoundException;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.repository.PostRepository;
import com.prgrms.coretime.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  public CommentCreateResponse createComment(CommentCreateRequest commentCreateRequest) {
    User user = new User("example.com", "it is unsafeUser"); // 이부분이 로그인 받아온 user가 되어야함.
    Post post = getPost(commentCreateRequest.getPostId());
    Comment parent = commentCreateRequest.getParentId() == null ?
        null : getParentComment(commentCreateRequest.getParentId());

    Comment comment = Comment.builder()
        .user(user)
        .post(post)
        .parent(parent)
        .isAnonymous(commentCreateRequest.getIsCommentAnonymous())
        .content(commentCreateRequest.getContent())
        .build();

    commentRepository.save(comment);

    return CommentCreateResponse.of(user, post, comment);
  }


  /**
   * TODO : Exception Message 관리 어떻게 할 것인지
   */
  private Post getPost(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(NotFoundException::new);
  }

  private Comment getParentComment(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(NotFoundException::new);
  }
}