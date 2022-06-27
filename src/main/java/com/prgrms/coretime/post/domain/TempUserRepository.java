package com.prgrms.coretime.post.domain;

import com.prgrms.coretime.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempUserRepository extends JpaRepository<User, Long> {

}
