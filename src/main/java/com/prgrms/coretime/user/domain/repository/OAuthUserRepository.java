package com.prgrms.coretime.user.domain.repository;

import com.prgrms.coretime.user.domain.OAuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {

}
