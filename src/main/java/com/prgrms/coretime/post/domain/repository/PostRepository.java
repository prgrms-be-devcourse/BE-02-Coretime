package com.prgrms.coretime.post.domain.repository;

import com.prgrms.coretime.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
