package com.prgrms.coretime.post.domain.repository;

import com.prgrms.coretime.post.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
