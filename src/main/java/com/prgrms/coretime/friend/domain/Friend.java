package com.prgrms.coretime.friend.domain;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.user.domain.TestUser;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinColumnOrFormula;

@Entity
@Table(name = "friend")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Friend extends BaseEntity {

  @EmbeddedId
  private FriendId friendId;

  @MapsId("followerId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumnOrFormula(column =
  @JoinColumn(name = "follower_id",
      referencedColumnName = "user_id")
  )
  private TestUser followerUser;

  @MapsId("followeeId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumnOrFormula(column =
  @JoinColumn(name = "followee_id",
      referencedColumnName = "user_id")
  )
  private TestUser followeeUser;

  public Friend(TestUser followerUser, TestUser followeeUser) {
    this.friendId = new FriendId(followerUser.getId(), followeeUser.getId());
    this.followerUser = followerUser;
    this.followeeUser = followeeUser;
  }
}
