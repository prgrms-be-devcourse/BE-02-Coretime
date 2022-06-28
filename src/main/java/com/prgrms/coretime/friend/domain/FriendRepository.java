package com.prgrms.coretime.friend.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRepository extends JpaRepository<Friend, FriendId> {

  @Query(
      value = "select * from friend where followee_id=:id "
          + "MINUS "
          + "select f1.followee_id as followee_id, f1.follower_id as follower_id, f1.created_at as created_at, f1.updated_at as updated_at from friend as f1 inner join friend as f2 on f1.followee_id=f2.follower_id where f1.followee_id=:id and f1.follower_id=f2.followee_id",
      nativeQuery = true
  )
  Page<Friend> findByFolloweeUser_Id(@Param("id") Long followeeId, Pageable pageable);
}
