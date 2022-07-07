package com.prgrms.coretime.user.domain.repository;

import com.prgrms.coretime.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

  @Query("select u from User u join fetch u.school s where u.email = :email and u.isQuit = :isQuit")
  Optional<User> findByEmailAndIsQuit(String email, Boolean isQuit);

  @Query("select u from User u join fetch u.school s where u.nickname = :nickname and u.isQuit = :isQuit")
  Optional<User> findByNicknameAndIsQuit(String nickname, Boolean isQuit);

  /*TODO : isQuit = false인 것으로 체크해야 함.*/
  boolean existsByEmailAndIsQuit(String email, Boolean isQuit);

  boolean existsByNicknameAndIsQuit(String nickname, Boolean isQuit);
}
