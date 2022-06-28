package com.prgrms.coretime.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<TestUser, Long> {

}
