package com.prgrms.coretime.user.domain.repository;

import com.prgrms.coretime.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface UserRepository extends JpaRepository<User, Long> {

  @Query("select u from User u join fetch u.school s where u.email = :email")
  Optional<User> findByEmail(String email);

  @Query("select u from User u join fetch u.school s where u.nickname = :nickname")
  Optional<User> findByNickname(String nickname);

  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);
}
