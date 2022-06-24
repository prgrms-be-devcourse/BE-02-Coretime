package com.prgrms.coretime.post.service;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardRepository;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.PostLike;
import com.prgrms.coretime.post.domain.PostLikeRepository;
import com.prgrms.coretime.post.domain.PostRepository;
import com.prgrms.coretime.post.dto.request.PostCreateRequest;
import com.prgrms.coretime.post.dto.request.PostUpdateRequest;
import com.prgrms.coretime.post.dto.response.PostIdResponse;
import com.prgrms.coretime.post.dto.response.PostResponse;
import com.prgrms.coretime.post.dto.response.PostSimpleResponse;
import com.prgrms.coretime.user.domain.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final PostLikeRepository postLikeRepository;

    public PostService(PostRepository postRepository, BoardRepository boardRepository,
        PostLikeRepository postLikeRepository) {
        this.postRepository = postRepository;
        this.boardRepository = boardRepository;
        this.postLikeRepository = postLikeRepository;
    }

    @Transactional(readOnly = true)
    public Page<PostSimpleResponse> getPostsByBoard(Long boardId, String keyword, Pageable pageable) {
        Page<Post> posts;
        if (Objects.isNull(keyword)) {
            posts = postRepository.findPostsByBoardId(boardId, pageable);
        } else {
            posts = postRepository.searchPostsAtBoard(keyword, boardId, pageable);
        }
        return posts.map(PostSimpleResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<PostSimpleResponse> getPostsByUser(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findPostsByUserId(userId, pageable);
        return posts.map(PostSimpleResponse::new);
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = findPost(postId);
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("created_at"));
        Page<Comment> comments = postRepository.findCommentsByPost(postId, pageRequest);
        return new PostResponse(post, comments);
    }

    @Transactional
    public PostIdResponse createPost(Long boardId, Long userId, PostCreateRequest request) {
        Board board = findBoard(boardId);
//        User user = findUser(userId)
        Post post = Post.builder()
                .board(board)
//                .user(user)
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
    public Page<PostSimpleResponse> searchPostsAtBoard(Long boardId, String keyword, Pageable pageable) {
        return postRepository.searchPostsAtBoard(keyword, boardId, pageable).map(PostSimpleResponse::new);
    }

    @Transactional
    public void likePost(Long userId, Long postId) {
        Optional<PostLike> postLike = postLikeRepository.findByUserIdAndPostId(userId, postId);
        if (postLike.isPresent()) {
            throw new IllegalArgumentException("해당 좋아요가 이미 존재합니다.");
        }
        Post post = findPost(postId);
        User user = findUser(userId);
        postLikeRepository.save(new PostLike(post, user));
    }

    @Transactional
    public void unlikePost(Long userId, Long postId) {
        Optional<PostLike> postLike = postLikeRepository.findByUserIdAndPostId(userId, postId);
        if (postLike.isEmpty()) {
            throw new IllegalArgumentException("해당 좋아요가 존재하지 않습니다.");
        }
        postLikeRepository.deleteByUserIdAndPostId(userId, postId);
    }

    private Board findBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(
            () -> new IllegalArgumentException("해당 ID의 게시판이 존재하지 않습니다.")
        );
    }

    private Post findPost(Long postId) {
        return postRepository.findPostById(postId).orElseThrow(
            () -> new IllegalArgumentException("해당 ID의 게시글이 존재하지 않습니다.")
        );
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다.")
        );
    }

    private PostLike findPostLike(Long postLikeId) {
        return postLikeRepository.findById(postLikeId).orElseThrow(
            () -> new IllegalArgumentException("해당 좋아요가 존재하지 않습니다.")
        );
    }
}
