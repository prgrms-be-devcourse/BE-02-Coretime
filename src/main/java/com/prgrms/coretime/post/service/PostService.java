package com.prgrms.coretime.post.service;

import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardRepository;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.PostRepository;
import com.prgrms.coretime.post.dto.request.PostCreateRequest;
import com.prgrms.coretime.post.dto.request.PostUpdateRequest;
import com.prgrms.coretime.post.dto.response.PostIdResponse;
import com.prgrms.coretime.post.dto.response.PostResponse;
import com.prgrms.coretime.post.dto.response.PostSimpleResponse;
import com.prgrms.coretime.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;

    public PostService(PostRepository postRepository, BoardRepository boardRepository) {
        this.postRepository = postRepository;
        this.boardRepository = boardRepository;
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
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 ID의 게시글이 존재하지 않습니다.")
        );
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("created_at"));
        Page<Comment> comments = postRepository.findCommentsByPost(postId, pageRequest);
        return new PostResponse(post, comments);
    }

    @Transactional
    public PostIdResponse createPost(Long boardId, Long userId, PostCreateRequest request) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new IllegalArgumentException("해당 ID의 게시판이 존재하지 않습니다.")
        );
//        User user = userRepository.findById(userId).orElseThrow(
//                () -> new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다.")
//        );
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
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 ID의 게시글이 존재하지 않습니다.")
        );
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
}
