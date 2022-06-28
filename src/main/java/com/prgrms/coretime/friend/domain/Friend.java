package com.prgrms.coretime.friend.domain;

import com.prgrms.coretime.common.entity.BaseEntity;
import com.prgrms.coretime.user.domain.User;
import java.util.Objects;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
  @ManyToOne
  @JoinColumnOrFormula(column =
  @JoinColumn(name = "follower_id",
      referencedColumnName = "user_id")
  )
  private User followerUser;

  @MapsId("followeeId")
  @ManyToOne
  @JoinColumnOrFormula(column =
  @JoinColumn(name = "followee_id",
      referencedColumnName = "user_id")
  )
  private User followeeUser;

  /*public void setFollowerUser(User followerUser) {
    if (Objects.nonNull(this.followerUser)) {
      followerUser.getFollowers().remove(this);
    }
    this.followerUser = followerUser;
    followerUser.getFollowers().add(this);
  }

  public void setFolloweeUser(User followeeUser) {
    if (Objects.nonNull(this.followeeUser)) {
      followeeUser.getFollowees().remove(this);
    }
    this.followeeUser = followeeUser;
    followeeUser.getFollowees().add(this);
  }*/
}
