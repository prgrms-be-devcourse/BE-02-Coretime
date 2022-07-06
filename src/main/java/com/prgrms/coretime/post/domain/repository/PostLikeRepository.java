package com.prgrms.coretime.post.domain.repository;

import com.prgrms.coretime.post.domain.PostLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

  @Query("select pl from PostLike pl where pl.user.id = :userId and pl.post.id = :postId")
  Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);

  @Modifying
  @Query("delete from PostLike pl where pl.user.id = :userId and pl.post.id = :postId")
  void deleteByUserIdAndPostId(Long userId, Long postId);
}
