package com.prgrms.coretime.user.domain.repository;

import com.prgrms.coretime.user.domain.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalUserRepository extends JpaRepository<LocalUser, Long> {

}
