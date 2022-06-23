package com.prgrms.coretime.timetable.domain.repository;

import com.prgrms.coretime.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

// 임시로 만든 UserRepository 입니다. 개발 중에만 사용하며 merge 후 사라질 예정 입니다.
// TODO : 머지 후 삭제
public interface TemporaryUserRepository extends JpaRepository<User, Long> {

}
