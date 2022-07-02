package com.prgrms.coretime.post.service;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.common.ErrorCode;
import com.prgrms.coretime.common.error.exception.AlreadyExistsException;
import com.prgrms.coretime.common.error.exception.NotFoundException;
import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.repository.BoardRepository;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.PostLike;
import com.prgrms.coretime.post.domain.repository.PostLikeRepository;
import com.prgrms.coretime.post.domain.repository.PostRepository;
import com.prgrms.coretime.post.dto.request.PostCreateRequest;
import com.prgrms.coretime.post.dto.request.PostUpdateRequest;
import com.prgrms.coretime.post.dto.response.PostIdResponse;
import com.prgrms.coretime.post.dto.response.PostResponse;
import com.prgrms.coretime.post.dto.response.PostSimpleResponse;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

  private final PostRepository postRepository;
  private final BoardRepository boardRepository;
  private final PostLikeRepository postLikeRepository;
  private final UserRepository userRepository;
  private final Integer HOT_COUNT = 10;
  private final Integer BEST_COUNT = 100;

  public PostService(PostRepository postRepository, BoardRepository boardRepository,
      PostLikeRepository postLikeRepository,
      UserRepository userRepository) {
    this.postRepository = postRepository;
    this.boardRepository = boardRepository;
    this.postLikeRepository = postLikeRepository;
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public Page<PostSimpleResponse> getPostsByBoard(Long boardId, Pageable pageable) {
    findBoard(boardId);
    Page<Post> posts = postRepository.findPostsByBoardId(boardId, pageable);
    return posts.map(PostSimpleResponse::new);
  }

  @Transactional(readOnly = true)
  public Page<PostSimpleResponse> getHotPosts(Pageable pageable) {
    Page<Post> posts = postRepository.findPostsByLikeCount(HOT_COUNT, pageable);
    return posts.map(PostSimpleResponse::new);
  }

  @Transactional(readOnly = true)
  public Page<PostSimpleResponse> getBestPosts(Pageable pageable) {
    Page<Post> posts = postRepository.findPostsByLikeCount(BEST_COUNT, pageable);
    return posts.map(PostSimpleResponse::new);
  }

  @Transactional(readOnly = true)
  public Page<PostSimpleResponse> getPostsByUser(Long userId, Pageable pageable) {
    findUser(userId);
    Page<Post> posts = postRepository.findPostsByUserId(userId, pageable);
    return posts.map(PostSimpleResponse::new);
  }

  @Transactional(readOnly = true)
  public Page<PostSimpleResponse> getPostsThatUserCommentedAt(Long userId, Pageable pageable) {
    List<Long> postIds = postRepository.findPostIdsThatUserCommentedAt(userId);
    Page<Post> posts = postRepository.findPostsThatUserCommentedAt(postIds, pageable);
    return posts.map(PostSimpleResponse::new);
  }

  @Transactional(readOnly = true)
  public PostResponse getPost(Long postId) {
    Post post = findPost(postId);
    PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("createdAt"));
    Page<Comment> comments = postRepository.findCommentsByPost(postId, pageRequest);
    return new PostResponse(post, comments);
  }

  @Transactional
  public PostIdResponse createPost(Long boardId, Long userId, PostCreateRequest request) {
    Board board = findBoard(boardId);
    User user = findUser(userId);
    Post post = Post.builder()
        .board(board)
        .user(user)
        .title(request.getTitle())
        .content(request.getContent())
        .isAnonymous(request.getIsAnonymous())
        .build();
    return new PostIdResponse(postRepository.save(post).getId());
  }

  @Transactional
  public PostIdResponse updatePost(Long postId, PostUpdateRequest request) {
    Post post = findPost(postId);
    post.updatePost(request);
    return new PostIdResponse(postRepository.save(post).getId());
  }

  @Transactional
  public void deletePost(Long postId) {
    postRepository.deleteById(postId);
  }

  @Transactional(readOnly = true)
  public Page<PostSimpleResponse> searchPosts(String keyword, Pageable pageable) {
    return postRepository.searchPosts(keyword, pageable).map(PostSimpleResponse::new);
  }

  @Transactional(readOnly = true)
  public Page<PostSimpleResponse> searchPostsAtBoard(Long boardId, String keyword,
      Pageable pageable) {
    return postRepository.searchPostsAtBoard(keyword, boardId, pageable)
        .map(PostSimpleResponse::new);
  }

  @Transactional
  public void likePost(Long userId, Long postId) {
    Optional<PostLike> postLike = postLikeRepository.findByUserIdAndPostId(userId, postId);
    if (postLike.isPresent()) {
      throw new AlreadyExistsException(ErrorCode.POST_LIKE_ALREADY_EXISTS);
    }
    Post post = findPost(postId);
    User user = findUser(userId);
    postLikeRepository.save(new PostLike(post, user));
  }

  @Transactional
  public void unlikePost(Long userId, Long postId) {
    Optional<PostLike> postLike = postLikeRepository.findByUserIdAndPostId(userId, postId);
    if (postLike.isEmpty()) {
      throw new NotFoundException(ErrorCode.POST_LIKE_NOT_FOUND);
    }
    Post post = findPost(postId);
    User user = findUser(userId);
    postLikeRepository.deleteByUserIdAndPostId(userId, postId);
    post.unlikePost();
  }

  private Board findBoard(Long boardId) {
    return boardRepository.findById(boardId).orElseThrow(
        () -> new NotFoundException(ErrorCode.BOARD_NOT_FOUND)
    );
  }

  private Post findPost(Long postId) {
    return postRepository.findPostById(postId).orElseThrow(
        () -> new NotFoundException(ErrorCode.POST_NOT_FOUND)
    );
  }

  private User findUser(Long userId) {
    return userRepository.findById(userId).orElseThrow(
        () -> new NotFoundException(ErrorCode.USER_NOT_FOUND)
    );
  }
}
