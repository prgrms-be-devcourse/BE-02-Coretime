package com.prgrms.coretime.post.domain;

import com.prgrms.coretime.comment.domain.Comment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

  @Query(
      value = "select p from Post p join fetch p.board join fetch p.user where p.board.id = :boardId",
      countQuery = "select p from Post p where p.board.id = :boardId"
  )
  Page<Post> findPostsByBoardId(@Param("boardId") Long boardId, Pageable pageable);

  @Query(
      value = "select p from Post p join fetch p.board join fetch p.user where p.likeCount >= :likeCount",
      countQuery = "select p from Post p where p.likeCount >= :likeCount"
  )
  Page<Post> findPostsByLikeCount(@Param("likeCount") Integer likeCount, Pageable pageable);

  @Query(
      value = "select p from Post p join fetch p.board join fetch p.user where p.user.id = :userId",
      countQuery = "select p from Post p where p.user.id = :userId"
  )
  Page<Post> findPostsByUserId(@Param("userId") Long userId, Pageable pageable);

  @Query("select p from Post p join fetch p.board join fetch p.user where p.id = :postId")
  Optional<Post> findPostById(@Param("postId") Long postId);

  @Query(value = "select c from Comment c join fetch c.user where c.post.id = :postId",
      countQuery = "select c from Comment c where c.post.id = :postId")
  Page<Comment> findCommentsByPost(@Param("postId") Long postId, Pageable pageable);

  @Query(value = "select p from Post p join fetch p.board join fetch p.user where p.title like :keyword or p.content like :keyword",
      countQuery = "select p from Post p where p.title like :keyword or p.content like :keyword")
  Page<Post> searchPosts(@Param("keyword") String keyword, Pageable pageable);

  @Query(value = "select p from Post p join fetch p.board join fetch p.user where p.board.id = :boardId and (p.title like :keyword or p.content like :keyword)",
      countQuery = "select p from Post p where p.board.id = :boardId and (p.title like :keyword or p.content like :keyword)")
  Page<Post> searchPostsAtBoard(@Param("keyword") String keyword, @Param("boardId") Long boardId,
      Pageable pageable);


}
